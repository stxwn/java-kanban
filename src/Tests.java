import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class Tests {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void setup() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.clearAllTasks();
    }

    @Test
    public void testCreateAndGetTask() {
        Task task = new Task("описание", "Название задачи");
        long taskId = taskManager.createTask(task);
        Task rTask = taskManager.getTaskById(taskId);
        assertEquals(rTask, task, "Полученная задача должна совпадать с оригинальной");
    }

    @Test
    public void testInvalidSubtaskIdsRemovedFromEpic() {
        Epic epic = new Epic("Описание эпика", "Название эпика");
        long epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Описание подзадачи 1", "Название подзадачи 1", epicId);
        Subtask subtask2 = new Subtask("Описание подзадачи 2", "Название подзадачи 2", epicId);
        long subtask1Id = taskManager.createSubtask(subtask1);
        long subtask2Id = taskManager.createSubtask(subtask2);
        taskManager.deleteSubtask(subtask1Id);
        Epic updatedEpic = taskManager.getEpicById(epicId);
        assertFalse(updatedEpic.getSubtaskIds().contains(subtask1Id), "Удалённая подзадача не должна быть в списке активных подзадач эпика");
        assertTrue(updatedEpic.getSubtaskIds().contains(subtask2Id), "Оставшаяся подзадача должна быть активной в эпике");
    }


    @Test
    public void testDeleteTask() {
        Task task = new Task("описание", "Название задачи");
        long taskId = taskManager.createTask(task);
        taskManager.deleteTask(taskId);

        try {
            taskManager.getTaskById(taskId);
            fail("Ожидалось исключение");
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetterImpactOnTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Исходное описание", "Исходное название");
        long taskId = taskManager.createTask(task);

        task.setName("Новоназванная задача");
        task.setDescription("Новое описание задачи");
        task.setStatus(TaskStatus.IN_PROGRESS);

        Task updatedTask = taskManager.getTaskById(taskId);
        assertEquals("Новоназванная задача", updatedTask.getName(), "Имя задачи должно быть обновлено");
        assertEquals("Новое описание задачи", updatedTask.getDescription(), "Описание задачи должно быть обновлено");
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus(), "Статус задачи должен быть обновлен");

        assertSame(task, updatedTask, "Объект задачи в менеджере должен быть тем же самым объектом");
    }


    @Test
    public void testUpdatingTaskFieldsThroughManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Исходное описание", "Исходное название");
        long taskId = taskManager.createTask(task);

        task.setName("Новоназванная задача");
        task.setDescription("Новое описание задачи");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(taskId);
        assertEquals("Новоназванная задача", updatedTask.getName(), "Имя задачи должно быть обновлено");
        assertEquals("Новое описание задачи", updatedTask.getDescription(), "Описание задачи должно быть обновлено");
    }

    @Test
    public void testCreateAndGetEpic() {
        Epic epic = new Epic("описание", "Название эпика");
        long epicId = taskManager.createEpic(epic);
        Epic rEpic = taskManager.getEpicById(epicId);
        assertEquals(rEpic, epic, "Полученный эпик должен совпадать с оригинальным");
    }


    @Test
    public void testCreateAndGetSubtask() {
        Epic epic = new Epic("описание", "Название эпика");
        long epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("описание", "Название подзадачи", epicId);
        long subtaskId = taskManager.createSubtask(subtask);
        Subtask rSubtask = taskManager.getSubtaskById(subtaskId);
        assertEquals(rSubtask, subtask, "Полученная подзадача должна совпадать с оригинальной");
    }

    @Test
    public void testUpdateEpicStatus() {
        Epic epic = new Epic("описание", "Название эпика");
        long epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("описание", "Название подзадачи 1", epicId);
        Subtask subtask2 = new Subtask("описание", "Название подзадачи 2", epicId);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpicById(epicId);
        assertEquals(updatedEpic.getStatus(), TaskStatus.IN_PROGRESS, "Статус эпика должен быть IN_PROGRESS");
    }


    @Test
    public void testHistoryAdditions() {
        Task task = new Task("описание", "Название задачи");
        long taskId = taskManager.createTask(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.contains(task), "Задача должна присутствовать в истории");
    }

    @Test
    public void testHistoryRemoval() {
        Task task = new Task("описание", "Название задачи");
        long taskId = taskManager.createTask(task);
        historyManager.add(task);
        historyManager.remove(taskId);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task), "Задача должна быть удалена из истории");
    }


    @Test
    public void testSubtaskStatusChange() {
        Epic epic = new Epic("описание", "Название эпика");
        long epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("описание", "Название подзадачи", epicId);
        long subtaskId = taskManager.createSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        Subtask updatedSubtask = taskManager.getSubtaskById(subtaskId);
        assertEquals(updatedSubtask.getStatus(), TaskStatus.DONE, "Статус подзадачи должен быть изменен");
    }

    @Test
    public void testEqualityById() {
        Task task1 = new Task("описание", "Название задачи 1");
        Task task2 = new Task("описание", "Название задачи 2");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Экземпляры Task с одинаковыми ID должны быть равны");
    }

    @Test
    public void testUniqueId() {
        Task task1 = new Task("описание", "Название задачи 1");
        Task task2 = new Task("описание", "Название задачи 2");
        long taskId1 = taskManager.createTask(task1);
        long taskId2 = taskManager.createTask(task2);
        assertNotEquals(taskId1, taskId2, "ID задач должны быть уникальны");
    }

    @Test
    public void testDataDeletion() {
        Epic epic = new Epic("описание", "Название эпика");
        long epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("описание", "Название подзадачи 1", epicId);
        Subtask subtask2 = new Subtask("описание", "Название подзадачи 2", epicId);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.deleteEpic(epicId);
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи должны быть удалены вместе с эпиком");
    }

}