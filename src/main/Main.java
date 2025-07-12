package main;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);
        TaskStatus status = TaskStatus.NEW;

        var task1Id = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", status));
        var task2Id = taskManager.createTask(new Task("Задача 2", "Описание задачи 2", status));

        var epicWithSubtasksId = taskManager.createEpic(new Epic("Эпик с подзадачами", "Описание эпика"));
        var epicWithoutSubtasksId = taskManager.createEpic(new Epic("Эпик без подзадач", "Описание эпика"));

        var subtask1Id = taskManager.createSubtask(
                new Subtask("Подзадача 1", "Описание подзадачи 1", status, epicWithSubtasksId)
        );
        var subtask2Id = taskManager.createSubtask(
                new Subtask("Подзадача 2", "Описание подзадачи 2", status, epicWithSubtasksId
                )
        );
        var subtask3Id = taskManager.createSubtask(
                new Subtask("Подзадача 3", "Описание подзадачи 3", status, epicWithSubtasksId
                )
        );

        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(epicWithSubtasksId);
        taskManager.getTaskById(epicWithoutSubtasksId);
        taskManager.getTaskById(subtask1Id);
        taskManager.getTaskById(subtask2Id);
        taskManager.getTaskById(subtask3Id);
        taskManager.getTaskById(task1Id);

        printHistory("История просмотров:", taskManager.getHistory());

        taskManager.deleteTask(task1Id);
        printHistory("История после удаления задачи 1:", taskManager.getHistory());

        taskManager.deleteEpic(epicWithSubtasksId);
        printHistory("История после удаления эпика с подзадачами:", taskManager.getHistory());
    }

    private static void printHistory(String message, List<Task> history) {
        System.out.println(message);
        if (history.isEmpty()) {
            System.out.println("История пуста");
        } else {
            history.stream()
                    .map(Task::toString)
                    .forEach(System.out::println);
        }
        System.out.println();
    }
}