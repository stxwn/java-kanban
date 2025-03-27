import java.util.ArrayList;

public class Subtask extends Task{
    private int epicId;
    private Epic epic;
    private ArrayList<Long> subtaskIds = new ArrayList<>();

    public Subtask(String name, String description, int epicId) {
        super(name, description);
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

    public void setEpicId(int epicId) {
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


}
