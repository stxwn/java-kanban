package main;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final List<Long> subtaskIds;
    private TaskManager taskManager;

    public Epic(String name, String description) {
        this(null, name, description, TaskStatus.NEW);
    }

    public Epic(Long id, String name, String description, TaskStatus status) {
        super(id, name, description, status, null, null);
        this.subtaskIds = new ArrayList<>();
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public List<Subtask> getSubtasks() {
        if (taskManager == null) {
            return List.of();
        }
        return subtaskIds.stream()
                .map(taskManager::getSubtaskById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Long> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(long subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
            updateStatus();
        }
    }

    public void removeSubtaskId(long subtaskId) {
        subtaskIds.remove(subtaskId);
        updateStatus();
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtaskIds.isEmpty()) {
            return null;
        }
        return getSubtasks().stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getDuration() {
        if (subtaskIds.isEmpty()) {
            return Duration.ZERO;
        }
        return getSubtasks().stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime start = getStartTime();
        return start != null ? start.plus(getDuration()) : null;
    }

    private void updateStatus() {
        if (subtaskIds.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        List<Subtask> subtasks = getSubtasks();
        boolean allNew = subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW);
        boolean allDone = subtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE);

        if (allNew) {
            setStatus(TaskStatus.NEW);
        } else if (allDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public boolean hasTimeOverlaps() {
        List<Subtask> subtasks = getSubtasks();
        for (int i = 0; i < subtasks.size(); i++) {
            for (int j = i + 1; j < subtasks.size(); j++) {
                if (isTimeOverlapping(subtasks.get(i), subtasks.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTimeOverlapping(Subtask a, Subtask b) {
        if (a.getStartTime() == null || b.getStartTime() == null) {
            return false;
        }
        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd = a.getEndTime();
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd = b.getEndTime();

        return aStart.isBefore(bEnd) && aEnd.isAfter(bStart);
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
    }

    @Override
    public String toString() {
        return String.format(
                "Epic{id=%d, name='%s', description='%s', status=%s, subtaskCount=%d, startTime=%s, duration=%s}",
                getId(), getName(), getDescription(), getStatus(), subtaskIds.size(), getStartTime(), getDuration()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}