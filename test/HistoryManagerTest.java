import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();

        task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        task.setId(1);

        epic = new Epic("Test Epic", "Test Epic Description");
        epic.setId(2);

        subtask = new Subtask("Test Subtask", "Test Description", TaskStatus.NEW, 2);
        subtask.setId(3);
    }

    @Test
    public void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой при создании");
    }

    @Test
    public void shouldNotAddDuplicatesToHistory() {
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну запись о задаче");
        assertEquals(task, history.get(0), "Задача в истории должна соответствовать добавленной");
    }

    @Test
    public void shouldRemoveTaskFromBeginningOfHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(task), "Задача должна быть удалена из начала истории");
        assertEquals(epic, history.get(0), "Первой должен быть эпик");
        assertEquals(subtask, history.get(1), "Последней должна быть подзадача");
    }

    @Test
    public void shouldRemoveTaskFromMiddleOfHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(epic.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(epic), "Эпик должен быть удален из середины истории");
        assertEquals(task, history.get(0), "Первой должна быть задача");
        assertEquals(subtask, history.get(1), "Последней должна быть подзадача");
    }

    @Test
    public void shouldRemoveTaskFromEndOfHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(subtask.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(subtask), "Подзадача должна быть удалена из конца истории");
        assertEquals(task, history.get(0), "Первой должна быть задача");
        assertEquals(epic, history.get(1), "Последним должен быть эпик");
    }

    @Test
    public void shouldReturnTasksInOrderTheyWereAdded() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи");
        assertEquals(task, history.get(0), "Первой должна быть задача");
        assertEquals(epic, history.get(1), "Вторым должен быть эпик");
        assertEquals(subtask, history.get(2), "Третьей должна быть подзадача");
    }

    @Test
    public void shouldNotFailWhenRemovingNonExistentTask() {
        historyManager.add(task);
        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна остаться неизменной");
        assertEquals(task, history.get(0), "Задача должна остаться в истории");
    }
}