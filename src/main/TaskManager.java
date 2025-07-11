package main;

import java.util.List;

public interface TaskManager {
    long createTask(Task task);

    long createEpic(Epic epic);

    long createSubtask(Subtask subtask);

    List<Task> getHistory();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

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

    void clearAllEpics();

    void clearAllSubtasks();

    List<Subtask> getSubtasksForEpic(long epicId);

    void updateEpicStatus(Epic epic);

    void addToPrioritized(Task task);

    void removeFromPrioritized(long id);

    List<Task> getPrioritizedTasks();

    boolean isTimeOverlap(Task newTask);
}