package main;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Long> subtaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Long id, String title, String description, TaskStatus status) {
        super(title, description);
        this.setId(id);
        this.setStatus(status);
    }

    public ArrayList<Long> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(long subtaskId) {
        if (subtaskId == this.getId()) {
            throw new IllegalArgumentException("ошибка");
        }
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(long subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
    }
}

