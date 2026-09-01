package tests;

import login.LoginBobyModel;
import login.LoginResponseModel;
import login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTests extends TestBase{

String username = "user123";
String password = "User12345";
String wrongPassword = "User1234";

    @Test
    @DisplayName("Успешная авторизация с валидными данными")
    public void successfulLoginTest(){
        LoginBobyModel loginData = new LoginBobyModel(username, password);
        LoginResponseModel loginResponse =
        given()
                .log().all()
                .contentType(JSON)
                .body(loginData)
                .basePath("/api/v1")
                .when()
                .post("/auth/token/")
                .then()
                .log().all()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas.registration/registration_schema.json"))
                .extract().as(LoginResponseModel.class);

        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String actualRefresh = loginResponse.refresh();
        String actualAccess = loginResponse.access();

        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).isNotEqualTo(actualAccess);
    }
    @Test
    @DisplayName("")
    public void wrongCredentialsLoginTest() {
        LoginBobyModel loginData = new LoginBobyModel(username, wrongPassword);
        String expectedDetailError = "Invalid username or password.";

        WrongCredentialsLoginResponseModel loginResponse =
                given()
                        .log().all()
                        .contentType(JSON)
                        .body(loginData)
                        .basePath("/api/v1")
                        .when()
                        .post("/auth/token/")
                        .then()
                        .log().all()
                        .statusCode(401)
                        .body(matchesJsonSchemaInClasspath("schemas/login/wrong_credentials_login_response_schema.json"))
                        .body("detail", notNullValue())
                        .extract().as(WrongCredentialsLoginResponseModel.class);

        String actualDetailError = loginResponse.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }
    }
