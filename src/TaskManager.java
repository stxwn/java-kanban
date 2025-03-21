import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    int nextId = 1;
    HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();


    public long createTask(Task task) {
        int newId = nextId++;
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }


    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }


    public void clearAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(long id) {
        return tasks.get(id);
    }


    public boolean updateTask(Task updatedTask) {
        int existingId = updatedTask.getId();
        if (existingId == 0 || !tasks.containsKey(existingId)) {
            return false;
        }
        tasks.replace(existingId, updatedTask);
        return true;
    }


    public boolean deleteTask(long id) {
        return tasks.remove(id) != null;
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
            boolean anyNew = false;

            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() == TaskStatus.DONE) {
                    continue;
                } else if (subtask.getStatus() == TaskStatus.NEW) {
                    anyNew = true;
                } else {
                    allDone = false;
                }
            }

            if (allDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (anyNew) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}