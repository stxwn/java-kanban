import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final long MAX_HISTORY_SIZE = 10;
    private Node<Task> head;
    private Node<Task> tail;
    private Map<Long, Node<Task>> taskNodes;

    public InMemoryHistoryManager() {
        taskNodes = new HashMap<Long, Node<Task>>();
    }

    public void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }


    public ArrayList<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            list.add(current.value);
            current = current.next;
        }
        return list;
    }


    @Override
    public void add(Task task) {
        long taskId = task.getId();
        Node<Task> existingNode = taskNodes.get(taskId);


        if (existingNode != null) {
            removeNode(existingNode);
        }


        Node<Task> newNode = new Node<>(task);
        linkFirst(newNode);
        taskNodes.put(taskId, newNode);
    }


    @Override
    public void remove(long id) {
        Node<Task> node = taskNodes.get(id);
        if (node != null) {
            removeNode(node);
            taskNodes.remove(id);
        }
    }


    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            result.add(current.value);
            current = current.next;
        }
        return result;
    }


    private void linkFirst(Node<Task> node) {
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }


    private void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}