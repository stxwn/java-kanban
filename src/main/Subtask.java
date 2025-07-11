package main;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private long epicId;

    public Subtask(String name, String description, TaskStatus status, long epicId) {
        super(name, description, status, null, null);
        this.epicId = epicId;
    }

    public Subtask(Long id, String name, String description, TaskStatus status,
                   long epicId, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }


    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.SUBTASK;
    }
}