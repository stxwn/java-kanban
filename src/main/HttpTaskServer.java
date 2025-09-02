package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(value));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            String dateString = in.nextString();
            return dateString != null && !dateString.equals("null") ? LocalDateTime.parse(dateString, formatter) : null;
        }
    }

    private static class TaskHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;
        private final Pattern taskIdPattern = Pattern.compile("/tasks/(\\d+)");
        private final Pattern epicIdPattern = Pattern.compile("/epics/(\\d+)");
        private final Pattern subtaskIdPattern = Pattern.compile("/subtasks/(\\d+)");
        private final Pattern epicSubtasksPattern = Pattern.compile("/epics/(\\d+)/subtasks");

        public TaskHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                switch (path) {
                    case "/tasks":
                        handleTasks(exchange, method);
                        break;
                    case "/epics":
                        handleEpics(exchange, method);
                        break;
                    case "/subtasks":
                        handleSubtasks(exchange, method);
                        break;
                    case "/history":
                        handleHistory(exchange, method);
                        break;
                    case "/prioritized":
                        handlePrioritized(exchange, method);
                        break;
                    default:
                        handleSpecificRoutes(exchange, method, path);
                        break;
                }
            } catch (Exception e) {
                sendError(exchange, "Internal server error: " + e.getMessage(), 500);
            }
        }

        private void handleSpecificRoutes(HttpExchange exchange, String method, String path) throws IOException {
            if (taskIdPattern.matcher(path).matches()) {
                handleTaskById(exchange, method, path);
            } else if (epicIdPattern.matcher(path).matches()) {
                handleEpicById(exchange, method, path);
            } else if (subtaskIdPattern.matcher(path).matches()) {
                handleSubtaskById(exchange, method, path);
            } else if (epicSubtasksPattern.matcher(path).matches()) {
                handleEpicSubtasks(exchange, method, path);
            } else {
                sendError(exchange, "Not found", 404);
            }
        }

        private void handleTasks(HttpExchange exchange, String method) throws IOException {
            switch (method) {
                case "GET":
                    List<Task> tasks = taskManager.getAllTasks();
                    sendResponse(exchange, gson.toJson(tasks), 200);
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    Task task = gson.fromJson(requestBody, Task.class);
                    long taskId = taskManager.createTask(task);
                    sendResponse(exchange, "{\"id\":" + taskId + "}", 201);
                    break;
                case "DELETE":
                    taskManager.clearAllTasks();
                    sendResponse(exchange, "", 201);
                    break;
                default:
                    sendError(exchange, "Method not allowed", 405);
            }
        }

        private void handleTaskById(HttpExchange exchange, String method, String path) throws IOException {
            long id = extractId(path);

            switch (method) {
                case "GET":
                    Task task = taskManager.getTaskById(id);
                    if (task == null) {
                        sendError(exchange, "Task not found", 404);
                    } else {
                        sendResponse(exchange, gson.toJson(task), 200);
                    }
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    Task updatedTask = gson.fromJson(requestBody, Task.class);
                    updatedTask.setId(id);
                    if (taskManager.updateTask(updatedTask)) {
                        sendResponse(exchange, "", 201);
                    } else {
                        sendError(exchange, "Task not found", 404);
                    }
                    break;
                case "DELETE":
                    if (taskManager.deleteTask(id)) {
                        sendResponse(exchange, "", 201);
                    } else {
                        sendError(exchange, "Task not found", 404);
                    }
                    break;
                default:
                    sendError(exchange, "Method not allowed", 405);
            }
        }

        private void handleEpics(HttpExchange exchange, String method) throws IOException {
            switch (method) {
                case "GET":
                    List<Epic> epics = taskManager.getAllEpics();
                    sendResponse(exchange, gson.toJson(epics), 200);
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    Epic epic = gson.fromJson(requestBody, Epic.class);
                    long epicId = taskManager.createEpic(epic);
                    sendResponse(exchange, "{\"id\":" + epicId + "}", 201);
                    break;
                case "DELETE":
                    taskManager.clearAllEpics();
                    sendResponse(exchange, "", 201);
                    break;
                default:
                    sendError(exchange, "Method not allowed", 405);
            }
        }

        private void handleEpicById(HttpExchange exchange, String method, String path) throws IOException {
            long id = extractId(path);

            switch (method) {
                case "GET":
                    Epic epic = taskManager.getEpicById(id);
                    if (epic == null) {
                        sendError(exchange, "Epic not found", 404);
                    } else {
                        sendResponse(exchange, gson.toJson(epic), 200);
                    }
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    Epic updatedEpic = gson.fromJson(requestBody, Epic.class);
                    updatedEpic.setId(id);
                    if (taskManager.updateEpic(updatedEpic)) {
                        sendResponse(exchange, "", 201);
                    } else {
                        sendError(exchange, "Epic not found", 404);
                    }
                    break;
                case "DELETE":
                    if (taskManager.deleteEpic(id)) {
                        sendResponse(exchange, "", 201);
                    } else {
                        sendError(exchange, "Epic not found", 404);
                    }
                    break;
                default:
                    sendError(exchange, "Method not allowed", 405);
            }
        }

        private void handleSubtasks(HttpExchange exchange, String method) throws IOException {
            switch (method) {
                case "GET":
                    List<Subtask> subtasks = taskManager.getAllSubtasks();
                    sendResponse(exchange, gson.toJson(subtasks), 200);
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                    long subtaskId = taskManager.createSubtask(subtask);
                    sendResponse(exchange, "{\"id\":" + subtaskId + "}", 201);
                    break;
                case "DELETE":
                    taskManager.clearAllSubtasks();
                    sendResponse(exchange, "", 201);
                    break;
                default:
                    sendError(exchange, "Method not allowed", 405);
            }
        }

        private void handleSubtaskById(HttpExchange exchange, String method, String path) throws IOException {
            long id = extractId(path);

            switch (method) {
                case "GET":
                    Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask == null) {
                        sendError(exchange, "Subtask not found", 404);
                    } else {
                        sendResponse(exchange, gson.toJson(subtask), 200);
                    }
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    Subtask updatedSubtask = gson.fromJson(requestBody, Subtask.class);
                    updatedSubtask.setId(id);
                    if (taskManager.updateSubtask(updatedSubtask)) {
                        sendResponse(exchange, "", 201);
                    } else {
                        sendError(exchange, "Subtask not found", 404);
                    }
                    break;
                case "DELETE":
                    if (taskManager.deleteSubtask(id)) {
                        sendResponse(exchange, "", 201);
                    } else {
                        sendError(exchange, "Subtask not found", 404);
                    }
                    break;
                default:
                    sendError(exchange, "Method not allowed", 405);
            }
        }

        private void handleEpicSubtasks(HttpExchange exchange, String method, String path) throws IOException {
            if (!method.equals("GET")) {
                sendError(exchange, "Method not allowed", 405);
                return;
            }

            long epicId = extractIdFromPath(path, "/epics/", "/subtasks");
            List<Subtask> subtasks = taskManager.getSubtasksForEpic(epicId);
            sendResponse(exchange, gson.toJson(subtasks), 200);
        }

        private void handleHistory(HttpExchange exchange, String method) throws IOException {
            if (!method.equals("GET")) {
                sendError(exchange, "Method not allowed", 405);
                return;
            }

            List<Task> history = taskManager.getHistory();
            sendResponse(exchange, gson.toJson(history), 200);
        }

        private void handlePrioritized(HttpExchange exchange, String method) throws IOException {
            if (!method.equals("GET")) {
                sendError(exchange, "Method not allowed", 405);
                return;
            }

            List<Task> prioritized = taskManager.getPrioritizedTasks();
            sendResponse(exchange, gson.toJson(prioritized), 200);
        }

        private long extractId(String path) {
            String[] parts = path.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        }

        private long extractIdFromPath(String path, String prefix, String suffix) {
            String idPart = path.replace(prefix, "").replace(suffix, "");
            return Long.parseLong(idPart);
        }

        private String readRequestBody(HttpExchange exchange) throws IOException {
            try (InputStream is = exchange.getRequestBody()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

        private void sendError(HttpExchange exchange, String message, int statusCode) throws IOException {
            String response = "{\"error\":\"" + message + "\"}";
            sendResponse(exchange, response, statusCode);
        }
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new TaskHandler(taskManager, gson));
    }

    public void start() {
        System.out.println("HTTP Task Server started on port " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("HTTP Task Server stopped");
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        final HttpTaskServer server = new HttpTaskServer();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
        }));
    }
}