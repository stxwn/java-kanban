import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @Test
    void checkTaskEqualById() {
        Task task1 = new Task("Описание задачи 1", "Задача 1");
        Task task2 = new Task("Описание задачи 2", "Задача 2");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Экземпляры Task с одинаковыми ID должны быть равны");
    }

    @Test
    void checkSubtaskEqualById() {
        Subtask subtask1 = new Subtask("Описание подзадачи 1", "Подзадача 1", 1);
        Subtask subtask2 = new Subtask("Описание подзадачи 2", "Подзадача 2", 1);

        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Экземпляры Subtask с одинаковыми ID должны быть равны");
    }

    @Test
    void checkEpic() {
        Epic epic = new Epic("Описание эпика", "Эпик 1");
        epic.setId(1);

        try {
            epic.addSubtaskId(epic.getId());
            throw new AssertionError("Эпик должен был выбросить исключение, но этого не произошло.");
        } catch (IllegalArgumentException e) {
            System.out.println("Исключение было выброшено, как и ожидалось.");
        }
    }

    @Test
    void checkSubtask() {
        Subtask subtask = new Subtask("Описание подзадачи", "Подзадача 1", 1);
        subtask.setId(1);

        try {
            subtask.setEpicId(subtask.getId());
            throw new AssertionError("должно быть исключение");
        } catch (IllegalArgumentException e) {
            System.out.println("Исключение было выброшено");
        }
    }

    @Test
    void checkTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager должен быть проинициализирован.");
        assertFalse(taskManager.getAllTasks().isEmpty(), "Список задач не должен быть пустым.");
    }

    @Test
    void checkHistoryManager() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task("Описание задачи", "Задача 1");
        long taskId = taskManager.createTask(task);

        historyManager.add(task);

        assertNotNull(historyManager, "инициализация");

        assertFalse(historyManager.getHistory().isEmpty(), "История просмотров не должна быть пустой");
    }



    @Test
    void checkId() {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Описание задачи 1", "Задача 1");
        Task task2 = new Task("Описание задачи 2", "Задача 2");

        long taskId1 = taskManager.createTask(task1);
        long taskId2 = taskManager.createTask(task2);

        assertNotEquals(taskId1, taskId2, "ID задач должны быть уникальными");
    }

}