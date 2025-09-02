package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            switch (path) {
                case "/tasks":
                    handleTasks(exchange, method);
                    break;
                case "/subtasks":
                    handleSubtasks(exchange, method);
                    break;
                case "/epics":
                    handleEpics(exchange, method);
                    break;
                case "/history":
                    handleHistory(exchange, method);
                    break;
                case "/prioritized":
                    handlePrioritized(exchange, method);
                    break;
                default:
                    if (path.startsWith("/tasks/")) {
                        handleTaskById(exchange, method, path);
                    } else if (path.startsWith("/subtasks/")) {
                        handleSubtaskById(exchange, method, path);
                    } else if (path.startsWith("/epics/")) {
                        if (path.endsWith("/subtasks")) {
                            handleEpicSubtasks(exchange, path);
                        } else {
                            handleEpicById(exchange, method, path);
                        }
                    } else {
                        sendResponse(exchange, "Not Found", 404);
                    }
            }
        } catch (Exception e) {
            sendError(exchange, "Internal Server Error: " + e.getMessage(), 500);
        }
    }

    private void handleTasks(HttpExchange exchange, String method) throws IOException {
        switch (method) {
            case "GET":
                List<Task> tasks = taskManager.getAllTasks();
                sendJsonResponse(exchange, tasks, 200);
                break;
            case "POST":
                Task newTask = parseBody(exchange, Task.class);
                long taskId = taskManager.createTask(newTask);
                sendJsonResponse(exchange, new IdResponse(taskId), 201);
                break;
            case "DELETE":
                taskManager.clearAllTasks();
                sendResponse(exchange, "", 200);
                break;
            default:
                sendError(exchange, "Method Not Allowed", 405);
        }
    }

    private void handleTaskById(HttpExchange exchange, String method, String path) throws IOException {
        long id = extractId(path);
        switch (method) {
            case "GET":
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    sendJsonResponse(exchange, task, 200);
                } else {
                    sendError(exchange, "Task not found", 404);
                }
                break;
            case "POST":
                Task updatedTask = parseBody(exchange, Task.class);
                updatedTask.setId(id);
                if (taskManager.updateTask(updatedTask)) {
                    sendResponse(exchange, "", 200);
                } else {
                    sendError(exchange, "Task not found", 404);
                }
                break;
            case "DELETE":
                if (taskManager.deleteTask(id)) {
                    sendResponse(exchange, "", 200);
                } else {
                    sendError(exchange, "Task not found", 404);
                }
                break;
            default:
                sendError(exchange, "Method Not Allowed", 405);
        }
    }

    private void handleSubtasks(HttpExchange exchange, String method) throws IOException {
        switch (method) {
            case "GET":
                List<Subtask> subtasks = taskManager.getAllSubtasks();
                sendJsonResponse(exchange, subtasks, 200);
                break;
            case "POST":
                Subtask newSubtask = parseBody(exchange, Subtask.class);
                long subtaskId = taskManager.createSubtask(newSubtask);
                sendJsonResponse(exchange, new IdResponse(subtaskId), 201);
                break;
            case "DELETE":
                taskManager.clearAllSubtasks();
                sendResponse(exchange, "", 200);
                break;
            default:
                sendError(exchange, "Method Not Allowed", 405);
        }
    }

    private void handleSubtaskById(HttpExchange exchange, String method, String path) throws IOException {
        long id = extractId(path);
        switch (method) {
            case "GET":
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask != null) {
                    sendJsonResponse(exchange, subtask, 200);
                } else {
                    sendError(exchange, "Subtask not found", 404);
                }
                break;
            case "POST":
                Subtask updatedSubtask = parseBody(exchange, Subtask.class);
                updatedSubtask.setId(id);
                if (taskManager.updateSubtask(updatedSubtask)) {
                    sendResponse(exchange, "", 200);
                } else {
                    sendError(exchange, "Subtask not found", 404);
                }
                break;
            case "DELETE":
                if (taskManager.deleteSubtask(id)) {
                    sendResponse(exchange, "", 200);
                } else {
                    sendError(exchange, "Subtask not found", 404);
                }
                break;
            default:
                sendError(exchange, "Method Not Allowed", 405);
        }
    }

    private void handleEpics(HttpExchange exchange, String method) throws IOException {
        switch (method) {
            case "GET":
                List<Epic> epics = taskManager.getAllEpics();
                sendJsonResponse(exchange, epics, 200);
                break;
            case "POST":
                Epic newEpic = parseBody(exchange, Epic.class);
                long epicId = taskManager.createEpic(newEpic);
                sendJsonResponse(exchange, new IdResponse(epicId), 201);
                break;
            case "DELETE":
                taskManager.clearAllEpics();
                sendResponse(exchange, "", 200);
                break;
            default:
                sendError(exchange, "Method Not Allowed", 405);
        }
    }

    private void handleEpicById(HttpExchange exchange, String method, String path) throws IOException {
        long id = extractId(path);
        switch (method) {
            case "GET":
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    sendJsonResponse(exchange, epic, 200);
                } else {
                    sendError(exchange, "Epic not found", 404);
                }
                break;
            case "POST":
                Epic updatedEpic = parseBody(exchange, Epic.class);
                updatedEpic.setId(id);
                if (taskManager.updateEpic(updatedEpic)) {
                    sendResponse(exchange, "", 200);
                } else {
                    sendError(exchange, "Epic not found", 404);
                }
                break;
            case "DELETE":
                if (taskManager.deleteEpic(id)) {
                    sendResponse(exchange, "", 200);
                } else {
                    sendError(exchange, "Epic not found", 404);
                }
                break;
            default:
                sendError(exchange, "Method Not Allowed", 405);
        }
    }

    private void handleEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        long epicId = extractId(path.replace("/subtasks", ""));
        List<Subtask> subtasks = taskManager.getSubtasksForEpic(epicId);
        sendJsonResponse(exchange, subtasks, 200);
    }

    private void handleHistory(HttpExchange exchange, String method) throws IOException {
        if (!"GET".equals(method)) {
            sendError(exchange, "Method Not Allowed", 405);
            return;
        }
        List<Task> history = taskManager.getHistory();
        sendJsonResponse(exchange, history, 200);
    }

    private void handlePrioritized(HttpExchange exchange, String method) throws IOException {
        if (!"GET".equals(method)) {
            sendError(exchange, "Method Not Allowed", 405);
            return;
        }
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        sendJsonResponse(exchange, prioritized, 200);
    }

    // Вспомогательные методы
    private <T> T parseBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, clazz);
    }

    private void sendJsonResponse(HttpExchange exchange, Object object, int statusCode) throws IOException {
        String response = gson.toJson(object);
        sendResponse(exchange, response, statusCode);
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, String message, int statusCode) throws IOException {
        ErrorResponse error = new ErrorResponse(message);
        sendJsonResponse(exchange, error, statusCode);
    }

    private long extractId(String path) {
        String[] parts = path.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    // Внутренние классы для ответов
    private static class IdResponse {
        private final long id;

        public IdResponse(long id) {
            this.id = id;
        }
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}