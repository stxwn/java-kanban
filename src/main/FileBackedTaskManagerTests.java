//package main;
//
//import main.*;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class FileBackedTaskManagerTest {
//    private File tempFile;
//    private FileBackedTaskManager manager;
//
//    @BeforeEach
//    public void setUp() throws IOException {
//        tempFile = File.createTempFile("test-tasks", ".csv");
//        manager = new FileBackedTaskManager(tempFile.getAbsolutePath(), new InMemoryHistoryManager());
//    }
//
//    @AfterEach
//    public void tearDown() {
//        tempFile.delete();
//    }
//
//    @Test
//    public void testSaveAndLoadEmptyManager() {
//        manager.save();
//        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, new InMemoryHistoryManager());
//
//        assertTrue(loadedManager.getAllTasks().isEmpty());
//        assertTrue(loadedManager.getAllEpics().isEmpty());
//        assertTrue(loadedManager.getAllSubtasks().isEmpty());
//    }
//
//    @Test
//    public void testSaveAndLoadTasksWithTime() {
//        LocalDateTime now = LocalDateTime.now();
//        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, now, 30);
//        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS, now.plusHours(1), 45);
//
//        manager.createTask(task1);
//        manager.createTask(task2);
//        manager.save();
//
//        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, new InMemoryHistoryManager());
//        List<Task> loadedTasks = loadedManager.getAllTasks();
//
//        assertEquals(2, loadedTasks.size());
//        assertTrue(loadedTasks.stream().anyMatch(t -> t.getName().equals("Task 1")));
//        assertTrue(loadedTasks.stream().anyMatch(t -> t.getName().equals("Task 2")));
//        assertEquals(Duration.ofMinutes(30), loadedManager.getTaskById(task1.getId()).getDuration());
//    }
//
//    @Test
//    public void testSaveAndLoadEpicWithSubtasks() {
//        Epic epic = new Epic("Epic", "Epic Description");
//        manager.createEpic(epic);
//
//        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", TaskStatus.NEW,
//                epic.getId(), LocalDateTime.now(), 30);
//        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", TaskStatus.DONE,
//                epic.getId(), LocalDateTime.now().plusHours(1), 45);
//
//        manager.createSubtask(subtask1);
//        manager.createSubtask(subtask2);
//        manager.save();
//
//        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, new InMemoryHistoryManager());
//
//        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
//        assertEquals(2, loadedEpic.getSubtaskIds().size());
//        assertEquals(TaskStatus.IN_PROGRESS, loadedEpic.getStatus());
//    }
//
//    @Test
//    public void testTimeOverlapCheckWhenLoading() {
//        LocalDateTime now = LocalDateTime.now();
//        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, now, 30);
//        manager.createTask(task1);
//        manager.save();
//
//        // Создаем новый менеджер и пытаемся загрузить задачу с пересекающимся временем
//        FileBackedTaskManager newManager = new FileBackedTaskManager(tempFile.getAbsolutePath(),
//                new InMemoryHistoryManager());
//        Task overlappingTask = new Task("Overlapping", "Desc", TaskStatus.NEW, now.plusMinutes(15), 30);
//
//        assertThrows(IllegalArgumentException.class, () -> newManager.createTask(overlappingTask));
//    }
//
//    @Test
//    public void testHistoryPreservation() {
//        Task task = new Task("Task", "Description", TaskStatus.NEW);
//        Epic epic = new Epic("Epic", "Description");
//
//        manager.createTask(task);
//        manager.createEpic(epic);
//
//        // Добавляем в историю
//        manager.getTaskById(task.getId());
//        manager.getEpicById(epic.getId());
//        manager.save();
//
//        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile, new InMemoryHistoryManager());
//        List<Task> history = loadedManager.getHistory();
//
//        assertEquals(2, history.size());
//        assertTrue(history.stream().anyMatch(t -> t.getId() == task.getId()));
//        assertTrue(history.stream().anyMatch(t -> t.getId() == epic.getId()));
//    }
//}