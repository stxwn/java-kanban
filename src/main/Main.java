package main;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        long task1Id = taskManager.createTask(new Task("Описание задачи 1", "Задача 1"));
        long task2Id = taskManager.createTask(new Task("Описание задачи 2", "Задача 2"));

        long epicWithSubtasksId = taskManager.createEpic(new Epic("Описание эпика с подзадачами", "Эпик с подзадачами"));
        long epicWithoutSubtasksId = taskManager.createEpic(new Epic("Описание эпика без подзадач", "Эпик без подзадач"));

        long subtask1Id = taskManager.createSubtask(new Subtask("Описание подзадачи 1", "Подзадача 1", epicWithSubtasksId));
        long subtask2Id = taskManager.createSubtask(new Subtask("Описание подзадачи 2", "Подзадача 2", epicWithSubtasksId));
        long subtask3Id = taskManager.createSubtask(new Subtask("Описание подзадачи 3", "Подзадача 3", epicWithSubtasksId));


        taskManager.getTaskById(task1Id);
        taskManager.getTaskById(task2Id);
        taskManager.getTaskById(epicWithSubtasksId);
        taskManager.getTaskById(epicWithoutSubtasksId);
        taskManager.getTaskById(subtask1Id);
        taskManager.getTaskById(subtask2Id);
        taskManager.getTaskById(subtask3Id);
        taskManager.getTaskById(task1Id);

        System.out.println("История просмотров:");
        List<Task> history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }

        taskManager.deleteTask(task1Id);
        System.out.println("История после удаления задачи 1:");
        history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }

        taskManager.deleteEpic(epicWithSubtasksId);
        System.out.println("История после удаления эпика с подзадачами:");
        history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }
    }
}