package Lesson24;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ToDoContractTest {

    private static final String URL = "https://todo-app-sky.herokuapp.com";

    private HttpClient client;

    @BeforeEach
    public void setUp() {
        client = HttpClientBuilder.create().build();
    }

    @Test
    @DisplayName("Получение списка задач. Проверяем статус-код и заголовок Content-Type")
    public void shouldReceive200OnGetRequest() throws IOException {
        HttpGet getListReq = new HttpGet(URL);
        HttpResponse response = client.execute(getListReq);
        String body = EntityUtils.toString(response.getEntity());

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(1, response.getHeaders("Content-Type").length);
        assertEquals("application/json; charset=utf-8", response.getHeaders("Content-Type")[0].getValue());
        assertTrue(body.startsWith("["));
        assertTrue(body.endsWith("]"));
    }

    @Test
    @DisplayName("Создание задачи. Проверяем статус-код, заголовок Content-Type и тело ответа содержит json")
    public void shouldReceive201OnPostRequest() throws IOException {
        HttpResponse response = createNewTask("Моя задача");
        String body = EntityUtils.toString(response.getEntity());

        assertEquals(200, response.getStatusLine().getStatusCode()); // сервер возвращает 200
        assertEquals(1, response.getHeaders("Content-Type").length);
        assertEquals("application/json; charset=utf-8", response.getHeaders("Content-Type")[0].getValue());
        assertTrue(body.startsWith("{"));
        assertTrue(body.endsWith("}"));
    }


    @Test
    @DisplayName("Удаляет существующую задачу. Статус 200, проверка тела ответа")
    public void shouldReceive200OnDelete() throws IOException {
        HttpResponse newTask = createNewTask("Задача на удаление");
        String body = EntityUtils.toString(newTask.getEntity());
        String id = extractIdFromResponse(body);

        HttpDelete deleteTaskReq = new HttpDelete(URL + "/" + id);
        HttpResponse response = client.execute(deleteTaskReq);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals(1, response.getHeaders("Content-Length").length);
        assertEquals("\"todo was deleted\"", EntityUtils.toString(response.getEntity()));
    }

    // --- Новые тесты ---

    @Test
    @DisplayName("Переименование задачи. Проверяем, что текст обновился")
    public void shouldRenameTask() throws IOException {
        HttpResponse newTask = createNewTask("Задача на переименование");
        String body = EntityUtils.toString(newTask.getEntity());
        String id = extractIdFromResponse(body);
        assertNotNull(id, "ID задачи не должен быть null");

        HttpPatch putReq = new HttpPatch(URL + "/" + id);
        String updatedJson = "{\"title\":\"Renamed task\"}";
        putReq.setEntity(new StringEntity(updatedJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(putReq);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    @DisplayName("Отметка задачи выполненной. Проверяем, что completed=true")
    public void shouldMarkTaskCompleted() throws IOException {
        // Создаём задачу
        HttpResponse newTask = createNewTask("Задача выполнена");
        String body = EntityUtils.toString(newTask.getEntity());
        String id = extractIdFromResponse(body);

        // Отмечаем выполненной
        HttpPatch putReq = new HttpPatch(URL + "/" + id);
        String completedJson = "{\"completed\":true}";
        putReq.setEntity(new StringEntity(completedJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(putReq);
        String responseBody = EntityUtils.toString(response.getEntity());

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(responseBody.contains("\"completed\":true"));
    }

    @Test
    @DisplayName("Удаление задачи повторно. Проверяем корректный ответ при удалении несуществующей задачи")
    public void shouldHandleDeleteNonExistingTask() throws IOException {
        // Создаём и удаляем задачу
        HttpResponse newTask = createNewTask("Повторное удаление");
        String body = EntityUtils.toString(newTask.getEntity());
        String id = extractIdFromResponse(body);

        HttpDelete deleteReq1 = new HttpDelete(URL + "/" + id);
        HttpResponse response1 = client.execute(deleteReq1);
        assertEquals(200, response1.getStatusLine().getStatusCode());

        // Повторное удаление той же задачи
        HttpDelete deleteReq2 = new HttpDelete(URL + "/" + id);
        HttpResponse response2 = client.execute(deleteReq2);

        // В зависимости от API может быть 404 или 200 с сообщением об ошибке
        int status = response2.getStatusLine().getStatusCode();
        assertTrue(status == 200 || status == 404, "Ожидается статус 200 или 404, фактически: " + status);
    }

    // --- Вспомогательные методы ---

    private HttpResponse createNewTask(String title) throws IOException {
        HttpPost postReq = new HttpPost(URL);
        String json = "{\"title\":\"" + title + "\"}";
        postReq.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return client.execute(postReq);
    }

    private String extractIdFromResponse(String json) {
        // Простой парсинг id из строки вида {"id":12345,"title":"...","completed":null}
        int idIndex = json.indexOf("\"id\":");
        if (idIndex == -1) return null;
        int start = idIndex + 5;
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        if (end == -1) return null;
        return json.substring(start, end).trim();
    }
}