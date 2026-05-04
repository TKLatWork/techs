package me.dslProject.userInfo;

import io.restassured.RestAssured;
import me.dslProject.userInfo.model.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public abstract class UserInfoAPITest {

    protected abstract int getPort();

    @BeforeEach
    void setUp() {
        RestAssured.port = getPort();
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void uc001_login_success() {
        given()
            .contentType("application/json")
            .body(new LoginRequest("admin", "password"))
        .when()
            .post("/api/users/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue());
    }

    @Test
    void uc001_login_invalidCredentials() {
        given()
            .contentType("application/json")
            .body(new LoginRequest("admin", "wrong"))
        .when()
            .post("/api/users/login")
        .then()
            .statusCode(401);
    }

    @Test
    void uc001_login_missingFields() {
        given()
            .contentType("application/json")
            .body(new LoginRequest(null, null))
        .when()
            .post("/api/users/login")
        .then()
            .statusCode(400);
    }

    @Test
    void uc002_getUserInfo_success() {
        String token = given()
            .contentType("application/json")
            .body(new LoginRequest("admin", "password"))
        .when()
            .post("/api/users/login")
        .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("token");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/users/info")
        .then()
            .statusCode(200)
            .body("userId", notNullValue())
            .body("username", equalTo("admin"))
            .body("role", notNullValue());
    }

    @Test
    void uc002_getUserInfo_missingAuth() {
        given()
        .when()
            .get("/api/users/info")
        .then()
            .statusCode(400);
    }

    @Test
    void uc002_getUserInfo_invalidToken() {
        given()
            .header("Authorization", "Bearer invalid.token.here")
        .when()
            .get("/api/users/info")
        .then()
            .statusCode(401);
    }
}
