package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        String response = gson.toJson(new ErrorResponse(message));
        sendText(exchange, response, 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        String response = gson.toJson(new ErrorResponse("Task has time overlaps with existing tasks"));
        sendText(exchange, response, 406);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        String response = gson.toJson(new ErrorResponse(message));
        sendText(exchange, response, 400);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = gson.toJson(new ErrorResponse("Method not allowed"));
        sendText(exchange, response, 405);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = gson.toJson(new ErrorResponse("Internal server error"));
        sendText(exchange, response, 500);
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}