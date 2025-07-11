package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private Map<Long, Node<Task>> taskNodes;

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;

        public Node(T value) {
            this.value = value;
        }
    }

    public InMemoryHistoryManager() {
        taskNodes = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        long taskId = task.getId();
        // Удаляем существующую ноду, если задача уже есть в истории
        if (taskNodes.containsKey(taskId)) {
            removeNode(taskNodes.get(taskId));
        }

        // Создаем новую ноду и добавляем в конец списка
        Node<Task> newNode = new Node<>(task);
        linkLast(newNode);
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

    private void linkLast(Node<Task> node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
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