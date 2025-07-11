package main;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected Long id;  // Изменили на Long для поддержки null
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected LocalDateTime startTime;
    protected Duration duration;

    // Основной конструктор
    public Task(Long id, String name, String description,
                TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.startTime = startTime;
        this.duration = duration != null ? duration : Duration.ZERO;
    }

    // Упрощенный конструктор
    public Task(String name, String description, TaskStatus status) {
        this(null, name, description, status, null, null);
    }

    public Task(String name, String description, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        this(null, name, description, status, startTime, duration);
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plus(duration) : null;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(long durationMinutes) {
        this.duration = Duration.ofMinutes(durationMinutes);
    }

    // Методы для работы с временем
    public boolean hasTime() {
        return startTime != null && !duration.isZero();
    }

    // Методы сравнения и преобразования
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() + " minutes" +
                ", endTime=" + getEndTime() +
                '}';
    }

    public String toCSV() {
        return String.join(",",
                String.valueOf(id),
                TypeOfTask.TASK.toString(),
                name,
                status.toString(),
                description,
                startTime != null ? startTime.toString() : "null",
                String.valueOf(duration.toMinutes())
        );
    }

    public TypeOfTask getType() {
        return TypeOfTask.TASK;
    }
}