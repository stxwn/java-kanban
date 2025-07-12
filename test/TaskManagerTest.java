import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    protected TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        long taskId = taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));
    }

    @Test
    public void testCreateSubtaskWithEpic() {
        Epic epic = new Epic("Test Epic", "Test Epic Description");
        long epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask Description",
                TaskStatus.NEW, epicId);
        long subtaskId = taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
        assertTrue(taskManager.getEpicById(epicId).getSubtaskIds().contains(subtaskId));
    }

    @Test
    public void testUpdateTaskStatus() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        long taskId = taskManager.createTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(taskId).getStatus());
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        long taskId = taskManager.createTask(task);
        taskManager.deleteTask(taskId);
        assertNull(taskManager.getTaskById(taskId));
    }

}