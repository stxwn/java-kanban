import java.util.HashMap;

public class TaskManager {
    int ID = 1;
    HashMap<Integer, Task> tasks = new HashMap<>();

    public int NewTask(Task task){
        int newID = ID++;
        task.setId(newID);
        tasks.put(newID, task);
        return newID;
    }
}
