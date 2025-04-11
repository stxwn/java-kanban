import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Task> historyMap = new HashMap<>();
    private ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            historyList.remove(historyMap.get(task.getId()));
        }

        historyList.add(task);
        historyMap.put(task.getId(), task);

        while (historyList.size() > 10) {
            Task oldestTask = historyList.remove(0);
            historyMap.remove(oldestTask.getId());
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList);
    }
}