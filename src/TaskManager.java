import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int nextId = 1;
    HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();

    // Методы для создания задач
    public long createTask(Task task) {
        int newId = nextId++;
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    public long createEpic(Epic epic) {
        int newId = nextId++;
        epic.setId(newId);
        tasks.put(newId, epic);
        return newId;
    }

    public long createSubtask(Subtask subtask) {
        int newId = nextId++;
        subtask.setId(newId);
        tasks.put(newId, subtask);
        return newId;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(long id) {
        return tasks.get(id);
    }

    public Epic getEpicById(long id) {
        return (Epic) tasks.get(id);
    }

    public Subtask getSubtaskById(long id) {
        return (Subtask) tasks.get(id);
    }

    public boolean updateTask(Task task) {
        int existingId = task.getId();
        if (existingId == 0 || !tasks.containsKey(existingId)) {
            return false;
        }
        tasks.replace(existingId, task);
        return true;
    }

    public boolean updateEpic(Epic epic) {
        int existingId = epic.getId();
        if (existingId == 0 || !tasks.containsKey(existingId)) {
            return false;
        }
        tasks.replace(existingId, epic);
        updateEpicStatus(epic);
        return true;
    }

    public boolean updateSubtask(Subtask subtask) {
        int existingId = subtask.getId();
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

    public boolean deleteTask(long id) {
        return tasks.remove(id) != null;
    }

    public boolean deleteSpecificTask(Task task) {
        return tasks.remove(task.getId()) != null;
    }

    public boolean deleteEpic(long id) {
        return tasks.remove(id) != null;
    }

    public boolean deleteSubtask(long id) {
        return tasks.remove(id) != null;
    }

    public void clearAllTasks() {
        tasks.clear();
    }

    public ArrayList<Subtask> getSubtasksForEpic(long epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask && ((Subtask) task).getEpicId() == epicId) {
                result.add((Subtask) task);
            }
        }
        return result;
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = getSubtasksForEpic(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            boolean allDone = true;
            boolean anyInProgress = false;

            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() == TaskStatus.DONE) {
                    continue;
                } else if (subtask.getStatus() == TaskStatus.NEW) {
                    if (!allDone) {
                        break;
                    }
                } else {
                    allDone = false;
                    anyInProgress = true;
                }
            }

            if (allDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (anyInProgress) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }
}