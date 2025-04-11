import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        long taskId = taskManager.createTask(new Task("Описание задачи 1", "Задача 1"));
        long epicId = taskManager.createEpic(new Epic("Описание эпика", "Эпик 1"));
        long subtaskId = taskManager.createSubtask(new Subtask("Описание подзадачи", "Подзадача 1", epicId));

        printAllTasks(taskManager);

        List<Task> history = taskManager.getHistory();
        System.out.println("История просмотров:");
        for (Task task : history) {
            System.out.println(task);
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Все задачи:");
        List<Task> allTasks = manager.getAllTasks();
        for (Task task : allTasks) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        List<Epic> epics = manager.getAllEpics();
        for (Epic epic : epics) {
            System.out.println(epic);

            List<Subtask> subtasks = manager.getSubtasksForEpic(epic.getId());
            for (Subtask subtask : subtasks) {
                System.out.println(subtask);
            }
        }

        System.out.println("Подзадачи:");
        List<Subtask> subtasks = manager.getAllSubtasks();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }

        System.out.println("История просмотров:");
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            System.out.println(task);
        }
    }
}