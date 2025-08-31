package restAssured.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import restAssured.enitites.User;

import static io.restassured.RestAssured.given;

public class AuthHelper {

    public AuthHelper() {
        RestAssured.baseURI = "https://innopolispython.onrender.com";
    }

    public String getToken(String username, String password) {
        return given().
                body(new User(username, password)).contentType(ContentType.JSON).
                when().
                post("/login").jsonPath().getString("token");
    }
}