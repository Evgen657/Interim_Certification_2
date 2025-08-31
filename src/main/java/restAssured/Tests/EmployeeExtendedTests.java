package restAssured.Tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import restAssured.enitites.EmployeeRequest;
import restAssured.enitites.EmployeeResponse;
import restAssured.helper.AuthHelper;
import restAssured.helper.EmployeeHelper;
import restAssured.helper.EmployeeHelperDB;

import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeExtendedTests {

    private static EmployeeHelper employeeHelper;
    private static EmployeeHelperDB employeeHelperDB;
    private static AuthHelper authHelper;
    private static String token;

    @BeforeAll
    public static void setup() throws SQLException, IOException {
        baseURI = "https://innopolispython.onrender.com";
        employeeHelper = new EmployeeHelper();
        employeeHelperDB = new EmployeeHelperDB();
        authHelper = new AuthHelper();
        token = authHelper.getToken("admin", "admin");
    }

    @Test
    @Order(1)
    @DisplayName("Создание сотрудника")
    public void createEmployee() throws Exception {
        int employeeId = employeeHelper.createEmployee(new EmployeeRequest("Moscow", "Evgen", "HR", "Batuev"));
        EmployeeResponse employee = employeeHelperDB.getEmployee(employeeId); // ИСПОЛЬЗОВАТЬ БД
        assertEquals(employeeId, employee.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Позитивный тест: Получение сотрудника по существующему ID")
    public void getEmployeeByIdPositive() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Moscow", "Evgen", "HR", "Batuev");
        int id = employeeHelper.createEmployee(request);
        assertTrue(id > 0);

        EmployeeResponse response = employeeHelper.getEmployeeById(id);
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getSurname(), response.getSurname());
        assertEquals(request.getCity(), response.getCity());
        assertEquals(request.getPosition(), response.getPosition());
    }

    @Test
    @Order(3)
    @DisplayName("Негативный тест: Получение сотрудника по несуществующему ID")
    public void getEmployeeByIdNegative() {
        int invalidId = 999999;
        EmployeeResponse response = employeeHelper.getEmployeeById(invalidId);
        assertNull(response);
    }

    @Test
    @Order(4)
    @DisplayName("Позитивный тест: Обновление сотрудника")
    public void updateEmployeePositive() throws Exception {
        EmployeeRequest createRequest = new EmployeeRequest("Moscow", "Evgen", "HR", "Batuev");
        int id = employeeHelper.createEmployee(createRequest);
        assertTrue(id > 0);

        EmployeeRequest updateRequest = new EmployeeRequest("Moscow", "Evgen", "IT", "Batuev");
        boolean updated = employeeHelper.updateEmployee(id, updateRequest);
        assertTrue(updated);

        EmployeeResponse empFromDb = employeeHelperDB.getEmployee(id);
        assertEquals("IT", empFromDb.getPosition());
    }

    @Test
    @Order(5)
    @DisplayName("Негативный тест: Обновление сотрудника с несуществующим ID")
    public void updateEmployeeInvalidId() {
        EmployeeRequest updateRequest = new EmployeeRequest("Kazan", "Noname", "Finance", "Batuev");
        boolean updated = employeeHelper.updateEmployee(999, updateRequest);
        assertFalse(updated);
    }

    @Test
    @Order(6)
    @DisplayName("Позитивный тест: Удаление сотрудника")
    public void deleteEmployeePositive() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Moscow", "Evgen", "IT", "Batuev");
        int id = employeeHelper.createEmployee(request);
        assertTrue(id > 0);

        boolean deleted = employeeHelper.deleteEmployee(id);
        assertTrue(deleted);

        EmployeeResponse empFromDb = employeeHelperDB.getEmployee(id);
        assertNull(empFromDb);
    }

    @Test
    @Order(7)
    @DisplayName("Негативный тест: Удаление сотрудника с несуществующим ID")
    public void deleteEmployeeInvalidId() {
        boolean deleted = employeeHelper.deleteEmployee(999);
        assertFalse(deleted);
    }

    @Test
    @Order(8)
    @DisplayName("Проверка создания сотрудников с одинаковыми именем и фамилией (разные ID)")
    public void noDuplicateEmployees() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Sochi", "Elena", "HR", "Petrova");

        // Создаем первого сотрудника
        int firstId = employeeHelper.createEmployee(request);
        assertTrue(firstId > 0);

        // Создаем второго сотрудника с теми же данными
        int secondId = employeeHelper.createEmployee(request);
        assertTrue(secondId > 0);

        // Проверяем, что ID разные
        assertNotEquals(firstId, secondId, "ID сотрудников должны быть уникальны");

        // Проверяем количество сотрудников с таким именем и фамилией в БД увеличилось на 2
        int count = employeeHelperDB.countEmployeesByNameAndSurname(request.getName(), request.getSurname());
        assertTrue(count >= 2, "Количество сотрудников с таким именем и фамилией должно быть не меньше 2");
    }

    @Test
    @Order(9)
    @DisplayName("Контрактный тест: Проверка схемы ответа при получении сотрудника")
    public void getEmployeeResponseSchema() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Moscow", "Evgen", "IT", "Batuev1");
        int id = employeeHelper.createEmployee(request);
        assertTrue(id > 0);

        given()
                .when()
                .get("/employee/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("city", isA(String.class))
                .body("name", isA(String.class))
                .body("surname", isA(String.class))
                .body("position", isA(String.class));
    }

    @Test
    @Order(10)
    @DisplayName("Негативный тест: Создание сотрудника без авторизации")
    public void createEmployeeWithoutAuth() {
        given()
                .contentType(ContentType.JSON)
                .body(new EmployeeRequest("Moscow", "Evgen", "IT", "Batuev"))
                .when()
                .post("/employee")
                .then()
                .statusCode(401);
    }

    @AfterEach
    public void cleanup() throws SQLException {
        employeeHelperDB.deleteTestEmployees();
    }
}