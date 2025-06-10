package main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTests {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setup() throws IOException {
        tempFile = File.createTempFile("test-tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile.getAbsolutePath(), new InMemoryHistoryManager());
    }

    @AfterEach
    public void cleanup() {
        tempFile.delete();
    }

    @Test
    public void testSavingAndLoadingEmptyFile() {
        manager.save();

        FileBackedTaskManager restoredManager = FileBackedTaskManager.loadFromFile(tempFile, new InMemoryHistoryManager());

        assertTrue(restoredManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(restoredManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(restoredManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    public void testSavingMultipleTasks() {
        Task task1 = new Task("описание", "Название задачи 1");
        Task task2 = new Task("описание", "Название задачи 2");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.save();

        assertTrue(tempFile.length() > 0, "Файл должен содержать данные");
    }

    @Test
    void shouldLoadMultipleTasksFromMemory() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile.getAbsolutePath(),
                new InMemoryHistoryManager());

        manager.createTask(new Task("Описание 1", "Задача 1"));
        manager.createTask(new Task("Описание 2", "Задача 2"));

        assertEquals(2, manager.getAllTasks().size(), "Менеджер должен содержать 2 задачи");
    }
}