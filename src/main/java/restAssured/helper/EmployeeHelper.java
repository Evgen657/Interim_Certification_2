package restAssured.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import restAssured.enitites.EmployeeRequest;
import restAssured.enitites.EmployeeResponse;

import static io.restassured.RestAssured.given;

public class EmployeeHelper {
    private final AuthHelper authHelper;

    public EmployeeHelper() {
        authHelper = new AuthHelper();
        RestAssured.baseURI = "https://innopolispython.onrender.com";
    }

    public EmployeeResponse getEmployeeById(int id) {
        Response response = given()
                .get("/employee/" + id)
                .andReturn();
        if (response.statusCode() == 200) {
            return response.as(EmployeeResponse.class);
        }
        return null;
    }

    public boolean updateEmployee(int id, EmployeeRequest request) {
        String token = authHelper.getToken("admin", "admin");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .put("/employee/" + id)
                .andReturn();
        return response.statusCode() == 200;
    }

    public int createEmployee(EmployeeRequest employee) {
        String token = authHelper.getToken("admin", "admin");

        Response response = given()
                .body(employee)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/employee")
                .andReturn();

        if (response.statusCode() == 201) {
            return response.jsonPath().getInt("id");
        } else {
            // Можно логировать ошибку для диагностики
            System.err.println("Failed to create employee: " + response.statusCode() + " " + response.getBody().asString());
            return -1;
        }
    }

    public EmployeeResponse getEmployee(int id) {
        Response response = given()
                .when()
                .get("/employee/" + id);
        try {
            return response.as(EmployeeResponse.class);
        } catch (IllegalStateException exception) {
            return new EmployeeResponse();
        }
    }

    public boolean deleteEmployee(int id) {
        String token = authHelper.getToken("admin", "admin");
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/employee/" + id)
                .andReturn();
        // Проверяем статус 200, а не 204
        return response.statusCode() == 200;
    }
}
