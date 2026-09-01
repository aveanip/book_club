package tests;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import login.LoginResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import registration.EmptyUsernameResponseModel;
import registration.ExistingUserResponseModel;
import registration.RegistrationBodyModel;
import registration.RegistrationResponseModel;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        long timestamp = System.currentTimeMillis();
        username = faker.name().firstName() + timestamp;
        password = faker.name().firstName() + timestamp;
    }


    @Test
    @DisplayName("")
    public void successfulRegisteringTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse =
                given()
                        .log().all()
                        .contentType(JSON)
                        .body(registrationData)
                        .basePath("/api/v1")
                        .when()
                        .post("/users/register/")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .body(matchesJsonSchemaInClasspath("schemas/login/login_response_schema.json"))
                        .body("id", notNullValue())
                        .body("username", notNullValue())
                        .body("remoteAddr", notNullValue())
                        .extract().as(RegistrationResponseModel.class);

        assertThat(registrationResponse).isEqualTo(username);
        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");
        // todo check remoteAddr value

    }


    @Test
    @DisplayName("Повторная регистрация с теми же данными возвращает статус 400 и сообщение об ошибке")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel firstregistrationResponse =
                given()
                        .log().all()
                        .contentType(JSON)
                        .body(registrationData)
                        .basePath("/api/v1")
                        .when()
                        .post("/users/register/")
                        .then()
                        .log().all()
                        .statusCode(201)
                        .body(matchesJsonSchemaInClasspath("schemas.registration/registration_schema.json"))
                        .body("id", notNullValue())
                        .body("username", notNullValue())
                        .body("remoteAddr", notNullValue())
                        .extract().as(RegistrationResponseModel.class);

        assertThat(firstregistrationResponse.username()).isEqualTo(username);

       ExistingUserResponseModel secondregistrationResponse =
                given()
                        .log().all()
                        .contentType(JSON)
                        .body(registrationData)
                        .basePath("/api/v1")
                        .when()
                        .post("/users/register/")
                        .then()
                        .log().all()
                        .statusCode(400)
                        .body(matchesJsonSchemaInClasspath("schemas.registration/existing_user_wrong_registration.json"))
                        .body("username", notNullValue())
                        .extract().as(ExistingUserResponseModel.class);

       String expectedError = "A user with that username already exists.";
       String actualError = secondregistrationResponse.username().get(0);
               assertThat(actualError).isEqualTo(expectedError);
    }
}
