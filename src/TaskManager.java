import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<Integer, Task>();

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
        Epic epic = subtask.getEpic();
        if (epic != null) {
            epic.addSubtaskId(newId);
        }
        return newId;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                epics.add((Epic) task);
            }
        }
        return epics;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                subtasks.add((Subtask) task);
            }
        }
        return subtasks;
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


    public boolean deleteEpic(long id) {
        Epic epic = getEpicById(id);
        if (epic != null) {

            ArrayList<Subtask> subtasks = getSubtasksForEpic(epic.getId());
            for (Subtask subtask : subtasks) {
                deleteSubtask(subtask.getId());
            }

            return tasks.remove(id) != null;
        }
        return false;
    }

    public boolean deleteSubtask(long id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic != null) {
                boolean removed = tasks.remove(id) != null;
                if (removed) {
                    updateEpicStatus(epic);
                    return true;
                }
            }
        }
        return false;
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
            boolean New = false;
            boolean InProgress = false;
            boolean Done = false;

            for (Subtask subtask : subtasks) {
                switch (subtask.getStatus()) {
                    case DONE:
                        Done = true;
                        break;
                    case IN_PROGRESS:
                        InProgress = true;
                        break;
                    case NEW:
                        New = true;
                        break;
                }

                if (Done || InProgress) {
                    break;
                }
            }

            if (Done) {
                epic.setStatus(TaskStatus.DONE);
            } else if (InProgress) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else if (New) {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }
}