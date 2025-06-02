package main;

import java.util.ArrayList;

public class Subtask extends Task {
    private long epicId;
    private Epic epic;
    private ArrayList<Long> subtaskIds = new ArrayList<>();

    public Subtask(String name, String description, long epicId) {
        super(name, description);
        this.epicId = (long) epicId;
    }

    public Subtask(Long id, String title, String description, TaskStatus status, Long epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public void addSubtaskId(long subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public Epic getEpic() {
        return epic;
    }


    public ArrayList<Long> getSubtaskIds() {
        return subtaskIds;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        if (epicId == this.getId()) {
            throw new IllegalArgumentException("ошибка");
        }
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public TypeOfTask getType() {
        return null;
    }
}
