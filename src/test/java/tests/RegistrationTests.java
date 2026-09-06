package tests;

import com.github.javafaker.Faker;
import models.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.registrationSpec.*;
import static tests.TestData.expectedError;
import static tests.TestData.expectedErrorFieldIsEmpty;

public class RegistrationTests extends TestBase {
    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName() + faker.number().randomNumber(6, false);
    }

    @Test
    @DisplayName("Успешная регистрация пользователя с валидными данными")
    public void successfulRegisteringTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse = step
                ("Отправка запроса на регистрацию нового пользователя", () ->
                        given(baseRequestSpec)
                                .body(registrationData)
                                .when()
                                .post("/users/register/")
                                .then()
                                .spec(successfulRegistrationRequestSpec)
                                .extract().as(RegistrationResponseModel.class)
                );
        step("Валидация полей ответа после успешной регистрации", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");
        });
    }

    @Test
    @DisplayName("Повторная регистрация с теми же данными возвращает статус 400 и сообщение об ошибке")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel firstregistrationResponse = step
                ("Первичная регистрация пользователя", () ->
                        given(baseRequestSpec)
                                .body(registrationData)
                                .when()
                                .post("/users/register/")
                                .then()
                                .spec(successfulRegistrationRequestSpec)
                                .extract().as(RegistrationResponseModel.class)
                );
        step("Проверка первичной успешной регистрации", () -> {
            assertThat(firstregistrationResponse.username()).isEqualTo(username);
        });
        ExistingUserResponseModel secondregistrationResponse = step
                ("Повторная регистрация с теме же данными", () -> {
                    return given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(existingUserWrongRequestSpec)
                            .extract().as(ExistingUserResponseModel.class);
                });
        step("Валидация сообщения об ошибки при повторной регистрации", () -> {
            String actualError = secondregistrationResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Регистрация без обязательного поля username")
    public void registrationWithoutUsername() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);
        ExistingUserResponseModel existingUserResponse = step
                ("Попытка регистрации с пустым username", () ->
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(usernameWrongRequestSpec)
                        .extract().as(ExistingUserResponseModel.class)
                );
        step ("Проверка сообщения об ошибке для поля username", () -> {
            String actualError = existingUserResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }


    @Test
    @DisplayName("Регистрация без обязательного поля password")
    public void registrationWithoutPassword() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");
        EmptyPasswordResponseModel emptyPasswordResponse =  step
                ("Попытка регистрации с пустым password", () ->
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(existingPasswordWrongRequestSpec)
                        .extract().as(EmptyPasswordResponseModel.class)
                );

        step ("Проверка сообщения об ошибке для поля password", () -> {
        String actualError = emptyPasswordResponse.password().get(0);
        assertThat(actualError).isEqualTo(expectedErrorFieldIsEmpty);
    });
    }

    @Test
    @DisplayName("Регистрация с пустыми обязательными полями username и password")
    public void registrationEmptyCredentials() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");
        EmptyCredentialsResponseModel emptyCredentialsResponse = step
                ("Попытка регистрации пользователя с пустыми полями username и password", () ->
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(emptyCredentialsRequestSpec)
                        .extract().as(EmptyCredentialsResponseModel.class)
                );

        step("Проверка сообщений об ошибках пустых полей", () -> {
        String actualUsernameError = emptyCredentialsResponse.username().get(0);
        String actualPasswordError = emptyCredentialsResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
        assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }
}


