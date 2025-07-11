package main;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected long nextId = 1L;
    protected final Map<Long, Task> tasks = new HashMap<>();
    protected final Map<Long, Epic> epics = new HashMap<>();
    protected final Map<Long, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).thenComparing(Task::getId)
    );

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void addToPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void removeFromPrioritized(long id) {
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTaskById(long id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public long createTask(Task task) {
        if (task == null) return -1;

        task.setId(nextId++);
        tasks.put(task.getId(), task);
        addToPrioritized(task);
        return task.getId();
    }

    @Override
    public long createEpic(Epic epic) {
        if (epic == null) return -1;

        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public long createSubtask(Subtask subtask) {
        if (subtask == null) return -1;

        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic с id " + subtask.getEpicId() + " не существует");
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
        addToPrioritized(subtask);

        return subtask.getId();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public boolean updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return false;
        }

        tasks.put(task.getId(), task);
        addToPrioritized(task);
        return true;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return false;
        }

        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return false;
        }

        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }

        addToPrioritized(subtask);
        return true;
    }

    @Override
    public boolean deleteTask(long id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(long id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Long subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
                historyManager.remove(subtaskId);
            }
            prioritizedTasks.removeIf(task -> task.getId() == id);
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubtask(long id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.removeIf(task -> task.getId() == id);
            historyManager.remove(id);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clearAllTasks() {
        new ArrayList<>(tasks.keySet()).forEach(this::deleteTask);
    }

    @Override
    public void clearAllEpics() {
        new ArrayList<>(epics.keySet()).forEach(this::deleteEpic);
    }

    @Override
    public void clearAllSubtasks() {
        new ArrayList<>(subtasks.keySet()).forEach(this::deleteSubtask);
    }

    @Override
    public List<Subtask> getSubtasksForEpic(long epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return Collections.emptyList();

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        List<Subtask> epicSubtasks = getSubtasksForEpic(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = epicSubtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW);
        boolean allDone = epicSubtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public boolean isTimeOverlap(Task newTask) {
        if (newTask == null || newTask.getStartTime() == null) return false;

        return prioritizedTasks.stream()
                .filter(task -> !task.equals(newTask))
                .filter(task -> task.getStartTime() != null)
                .anyMatch(existingTask -> isTasksOverlap(existingTask, newTask));
    }

    private boolean isTasksOverlap(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }
}