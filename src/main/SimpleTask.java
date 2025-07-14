package main;

import java.time.Duration;
import java.time.LocalDateTime;

public class SimpleTask extends Task {
    public SimpleTask(Long id, String title, String description, TaskStatus status,
                      LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
    }

    public SimpleTask(Long id, String title, String description, TaskStatus status) {
        this(id, title, description, status, null, Duration.ZERO);
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.TASK;
    }
}