import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("first task", "first task description");
        Task task2 = new Task("second task", "second task description");

        Epic epic1 = new Epic("first epic", "first epic description");
        Epic epic2 = new Epic("second epic", "second epic description");

        Subtask subtask1 = new Subtask("first subtask", "first subtask description", epic1.getId());
        Subtask subtask2 = new Subtask("second subtask", "second subtask description", epic2.getId());

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        System.out.println("all tasks:");
        ArrayList<Task> allTasks = manager.getAllTasks();
        for (Task task : allTasks) {
            System.out.println(task.toString());
        }

        System.out.println("all epics:");
        ArrayList<Epic> epics = new ArrayList<>();
        for (Task task : allTasks) {
            if (task instanceof Epic) {
                epics.add((Epic) task);
            }
        }
        for (Epic epic : epics) {
            System.out.println(epic.toString());
        }

        System.out.println("Все подзадачи:");
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task instanceof Subtask) {
                subtasks.add((Subtask) task);
            }
        }
        for (Subtask sub : subtasks) {
            System.out.println(sub.toString());
        }

        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.NEW);

        manager.updateTask(task1);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        System.out.println("updates:");
        System.out.println("task 1: " + task1.getStatus().toString());
        System.out.println("subtask 1: " + subtask1.getStatus().toString());
        System.out.println("sabtask 2: " + subtask2.getStatus().toString());

        manager.updateEpicStatus(epic1);
        manager.updateEpicStatus(epic2);

        System.out.println("status:");
        System.out.println("epic 1: " + epic1.getStatus().toString());
        System.out.println("epic 2: " + epic2.getStatus().toString());

        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic2.getId());

    }
}