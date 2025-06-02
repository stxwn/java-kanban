package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filePath;


    public FileBackedTaskManager(String filePath, HistoryManager historyManager) {
        super(historyManager);
        this.filePath = filePath;
        loadDataFromFile();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(filePath)) {
            List<String> lines = new ArrayList<>();
            for (Task task : getAllTasks()) {
                lines.add(task.toString());
            }
            writer.write(String.join("\n", lines));
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл " + filePath);
        }
    }

    private void loadDataFromFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = Task.fromString(line);
                tasks.put(task.getId(), task);
            }
        } catch (IOException | IllegalArgumentException ex) {
            System.out.println("Ошибка при загрузке данных из файла: " + ex.getMessage());
        }
    }

    @Override
    public long createTask(Task task) {
        long id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public long createEpic(Epic epic) {
        long id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public long createSubtask(Subtask subtask) {
        long id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean updated = super.updateTask(task);
        if (updated) save();
        return updated;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean updated = super.updateEpic(epic);
        if (updated) save();
        return updated;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean updated = super.updateSubtask(subtask);
        if (updated) save();
        return updated;
    }

    @Override
    public boolean deleteTask(long id) {
        boolean deleted = super.deleteTask(id);
        if (deleted) save();
        return deleted;
    }

    @Override
    public boolean deleteEpic(long epicId) {
        boolean deleted = super.deleteEpic(epicId);
        if (deleted) save();
        return deleted;
    }

    @Override
    public boolean deleteSubtask(long subtaskId) {
        boolean deleted = super.deleteSubtask(subtaskId);
        if (deleted) save();
        return deleted;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getAbsolutePath(), historyManager);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Processing line: " + line);
                Task task = Task.fromString(line);
                manager.tasks.put(task.getId(), task);
            }
        } catch (IOException | IllegalArgumentException ex) {
            System.out.println("Ошибка при загрузке данных из файла: " + ex.getMessage());
        }
        return manager;
    }
}

