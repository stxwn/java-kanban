package main;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    long id;
    TaskStatus status;

    public Task(String description, String name) {
        this.description = description;
        this.name = name;
        this.status = TaskStatus.NEW;
    }

    public Task(Long id, String title, String description, TaskStatus status) {
    }


    public static Task fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Некорректный формат строки задачи: " + line);
        }
        Long id = Long.parseLong(parts[0]);
        String name = parts[1];
        String description = parts[2];
        TaskStatus status = parts[3].equalsIgnoreCase("null") ? TaskStatus.NEW : TaskStatus.valueOf(parts[3]);
        return new Task(description, name);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Task)) {
            return false;
        }
        Task other = (Task) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + "," + name + "," + description + "," + status;
    }

    public TypeOfTask getType() {
        return null;
    }
}