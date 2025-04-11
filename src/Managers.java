public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    private Managers() {}

    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager(getDefaultHistory());
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }

    public static void setTaskManager(TaskManager newTaskManager) {
        taskManager = newTaskManager;
    }

    public static void setHistoryManager(HistoryManager newHistoryManager) {
        historyManager = newHistoryManager;
    }
}