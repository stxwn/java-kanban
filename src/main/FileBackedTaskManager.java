package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
        loadFromFile();
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, Managers.getDefaultHistory());
        manager.loadFromFile();
        return manager;
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        String[] fields = {
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                task.getStartTime() != null ? task.getStartTime().toString() : "null",
                String.valueOf(task.getDuration().toMinutes()),
                task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : ""
        };
        return String.join(",", fields);
    }

    private void loadFromFile() {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            reader.readLine(); // Пропускаем заголовок

            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task != null) {
                    tasks.put(task.getId(), task);
                    updateNextId(task.getId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 8) return null;

        long id = Long.parseLong(parts[0]);
        TypeOfTask type = TypeOfTask.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = "null".equals(parts[5]) ? null : LocalDateTime.parse(parts[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[6]));

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, startTime, duration);
                return task;
            case EPIC:
                Epic epic = new Epic(id, name, description, status);
                return epic;
            case SUBTASK:
                long epicId = Long.parseLong(parts[7]);
                Subtask subtask = new Subtask(id, name, description, status, epicId, startTime, duration);
                return subtask;
            default:
                return null;
        }
    }

    private void updateNextId(long id) {
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // Переопределенные методы для автоматического сохранения
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


}