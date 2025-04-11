import java.util.ArrayList;

public interface TaskManager {
    long createTask(Task task);
    long createEpic(Epic epic);
    long createSubtask(Subtask subtask);

    ArrayList<Task> getHistory();

    ArrayList<Task> getAllTasks();
    ArrayList<Epic> getAllEpics();
    ArrayList<Subtask> getAllSubtasks();

    Task getTaskById(long id);
    Epic getEpicById(long id);
    Subtask getSubtaskById(long id);

    boolean updateTask(Task task);
    boolean updateEpic(Epic epic);
    boolean updateSubtask(Subtask subtask);

    boolean deleteTask(long id);
    boolean deleteEpic(long id);
    boolean deleteSubtask(long id);

    void clearAllTasks();

    ArrayList<Subtask> getSubtasksForEpic(long epicId);
    void updateEpicStatus(Epic epic);
}