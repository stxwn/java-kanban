public class Task {
    String name;
    String  Description;
    int id;
    TaskStatus status;

    public Task(String description, String name) {
        Description = description;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return Description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", Description='" + Description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
