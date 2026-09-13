package tests;

import Api.UserApiClient;
import com.github.javafaker.Faker;
import models.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
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
        RegistrationResponseModel registrationResponse =
                api.user.successfulUserRegistration(registrationData);
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
        RegistrationResponseModel firstregistrationResponse =
                api.user.successfulUserRegistration(registrationData);
        step("Проверка первичной успешной регистрации", () -> {
            assertThat(firstregistrationResponse.username()).isEqualTo(username);
        });
        ExistingUserResponseModel secondregistrationResponse =
                api.user.secondregistrationResponse(registrationData);
        step("Валидация сообщения об ошибки при повторной регистрации", () -> {
            String actualError = secondregistrationResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedError);
        });
    }

    @Test
    @DisplayName("Регистрация без обязательного поля username")
    public void registrationWithoutUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);
        ExistingUserResponseModel existingUserResponse =
                api.user.registrationWithoutFieldUsername(registrationData);
        step ("Проверка сообщения об ошибке для поля username", () -> {
            String actualError = existingUserResponse.username().get(0);
            assertThat(actualError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }


    @Test
    @DisplayName("Регистрация без обязательного поля password")
    public void registrationWithoutPasswordTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");
        EmptyPasswordResponseModel emptyPasswordResponse =
                api.user.registrationWithoutFieldPassword(registrationData);

        step ("Проверка сообщения об ошибке для поля password", () -> {
        String actualError = emptyPasswordResponse.password().get(0);
        assertThat(actualError).isEqualTo(expectedErrorFieldIsEmpty);
    });
    }

    @Test
    @DisplayName("Регистрация с пустыми обязательными полями username и password")
    public void registrationEmptyCredentialsTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");
        EmptyCredentialsResponseModel emptyCredentialsResponse =
                api.user.emptyCredentialsUsernameAndPassword(registrationData);

        step("Проверка сообщений об ошибках пустых полей", () -> {
        String actualUsernameError = emptyCredentialsResponse.username().get(0);
        String actualPasswordError = emptyCredentialsResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
        assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }
}


