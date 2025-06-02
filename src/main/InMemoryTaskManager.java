package main;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private long nextId = 1L;
    HashMap<Long, Task> tasks = new HashMap<>();
    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task getTaskById(long id) {
        Task task = tasks.get(id);
        if (task == null) {
            System.err.println("Задача с ID " + id + " не найдена");
            throw new RuntimeException("Задача с id " + id + " не найдена.");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = (Epic) tasks.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = (Subtask) tasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) historyManager.getHistory();
    }

    @Override
    public long createTask(Task task) {
        long newId = nextId++;
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    @Override
    public long createEpic(Epic epic) {
        long newId = nextId++;
        epic.setId(newId);
        tasks.put(newId, epic);
        return newId;
    }

    @Override
    public long createSubtask(Subtask subtask) {
        if (!tasks.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic с id " + subtask.getEpicId() + " не существует");
        }

        long newId = nextId++;
        subtask.setId(newId);
        tasks.put(newId, subtask);

        Epic epic = (Epic) tasks.get(subtask.getEpicId());
        epic.addSubtaskId(newId);
        updateEpicStatus(epic);

        tasks.put(epic.getId(), epic);

        return newId;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                epics.add((Epic) task);
            }
        }
        return epics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                subtasks.add((Subtask) task);
            }
        }
        return subtasks;
    }

    @Override
    public boolean updateTask(Task task) {
        long existingId = task.getId();
        if (existingId == 0 || !tasks.containsKey(existingId)) {
            return false;
        }
        tasks.replace(existingId, task);
        return true;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        long existingId = epic.getId();
        if (existingId == 0 || !tasks.containsKey(existingId)) {
            return false;
        }
        tasks.replace(existingId, epic);
        updateEpicStatus(epic);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        long existingId = subtask.getId();
        if (existingId == 0 || !tasks.containsKey(existingId)) {
            return false;
        }
        tasks.replace(existingId, subtask);
        Epic epic = (Epic) tasks.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return true;
    }

    @Override
    public boolean deleteTask(long id) {
        return tasks.remove(id) != null;
    }

    @Override
    public boolean deleteEpic(long epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            ArrayList<Subtask> subtasks = getSubtasksForEpic(epicId);
            for (Subtask subtask : subtasks) {
                deleteSubtask(subtask.getId());
            }
            tasks.remove(epicId);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubtask(long subtaskId) {
        Subtask subtask = getSubtaskById(subtaskId);
        if (subtask != null) {
            long epicId = subtask.getEpicId();
            Epic epic = getEpicById(epicId);
            if (epic != null) {
                epic.getSubtaskIds().remove(Long.valueOf(subtaskId));
            }
            tasks.remove(subtaskId);
            return true;
        }
        return false;
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public ArrayList<Subtask> getSubtasksForEpic(long epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask && ((Subtask) task).getEpicId() == epicId) {
                result.add((Subtask) task);
            }
        }
        return result;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = getSubtasksForEpic(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            boolean hasDones = false;
            boolean hasInProgresses = false;
            boolean hasNews = false;

            for (Subtask subtask : subtasks) {
                TaskStatus status = subtask.getStatus();
                if (status != null) {
                    switch (status) {
                        case DONE:
                            hasDones = true;
                            break;
                        case IN_PROGRESS:
                            hasInProgresses = true;
                            break;
                        case NEW:
                            hasNews = true;
                            break;
                    }
                }
            }

            if (hasInProgresses) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else if (hasDones && !hasNews) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }
}