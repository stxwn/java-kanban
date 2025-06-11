package main;

public class SimpleTask extends Task {
    public SimpleTask(Long id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.TASK;
    }
}

