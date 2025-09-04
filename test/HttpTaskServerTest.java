import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.handlers.HttpTaskServer;
import main.managers.InMemoryTaskManager;
import main.managers.Managers;
import main.managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer server;
    private TaskManager taskManager;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        server = new HttpTaskServer(taskManager);

        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void testServerStartsAndStops() throws IOException {
        assertNotNull(server);
        assertNotNull(taskManager);

        assertDoesNotThrow(() -> {
            server.start();
            Thread.sleep(100);
            server.stop();
        });
    }

    @Test
    void testGetEmptyTasks() throws IOException, InterruptedException {
        server.start();
        try {
            Thread.sleep(100); // Даем серверу время на запуск

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            assertEquals("[]", response.body().trim());
        } finally {
            server.stop();
        }
    }


    @Test
    void testGetNonExistentTask() throws IOException, InterruptedException {
        server.start();
        try {
            Thread.sleep(100);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/999"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } finally {
            server.stop();
        }
    }

    @Test
    void testGetEmptyHistory() throws IOException, InterruptedException {
        server.start();
        try {
            Thread.sleep(100);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/history"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals("[]", response.body().trim());
        } finally {
            server.stop();
        }
    }

    @Test
    void testMethodNotAllowed() throws IOException, InterruptedException {
        server.start();
        try {
            Thread.sleep(100);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/history"))
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(405, response.statusCode());
        } finally {
            server.stop();
        }
    }

    @Test
    void testQuickPing() throws IOException, InterruptedException {
        server.start();
        try {
            Thread.sleep(100);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertTrue(response.statusCode() == 200 || response.statusCode() == 500);
        } finally {
            server.stop();
        }
    }
}



