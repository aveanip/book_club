package tests;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import models.registration.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.registrationSpec.*;

public class RegistrationTests extends TestBase {
    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName()+faker.number().randomNumber(6, false);
    }

    @Test
    @DisplayName("Успешная регистрация пользователя с валидными данными")
    public void successfulRegisteringTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(successfulRegistrationRequestSpec)
                        .extract().as(RegistrationResponseModel.class);

        assertThat(registrationResponse.username()).isEqualTo(username);
        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");
    }

    @Test
    @DisplayName("Повторная регистрация с теми же данными возвращает статус 400 и сообщение об ошибке")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel firstregistrationResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(successfulRegistrationRequestSpec)
                        .extract().as(RegistrationResponseModel.class);

        assertThat(firstregistrationResponse.username()).isEqualTo(username);

        ExistingUserResponseModel secondregistrationResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(existingUserWrongRequestSpec)
                        .extract().as(ExistingUserResponseModel.class);

        String expectedError = "A user with that username already exists.";
        String actualError = secondregistrationResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    @DisplayName("Регистрация без обязательного поля username")
    public void registrationWithoutUsername() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);
        ExistingUserResponseModel existingUserResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(usernameWrongRequestSpec)
                        .extract().as(ExistingUserResponseModel.class);

        String expectedError = "This field may not be blank.";
        String actualError = existingUserResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }


    @Test
    @DisplayName("Регистрация без обязательного поля password")
    public void registrationWithoutPassword() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");
        EmptyPasswordResponseModel emptyPasswordResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(existingPasswordWrongRequestSpec)
                        .extract().as(EmptyPasswordResponseModel.class);

        String expectedError = "This field may not be blank.";
        String actualError = emptyPasswordResponse.password().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @Test
    @DisplayName("Регистрация с пустыми обязательными полями username и password")
    public void registrationEmptyCredentials() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");
        EmptyCredentialsResponseModel emptyCredentialsResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(emptyCredentialsRequestSpec)
                        .extract().as(EmptyCredentialsResponseModel.class);

        String expectedUsernameError = "This field may not be blank.";
        String expectedPasswordError = "This field may not be blank.";
        String actualUsernameError = emptyCredentialsResponse.username().get(0);
        String actualPasswordError = emptyCredentialsResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }
}


