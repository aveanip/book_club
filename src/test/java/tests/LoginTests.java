package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;


public class LoginTests extends TestBase {
    @Test
    @DisplayName("Успешная авторизация с валидными данными")
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = step("Авторизация и получение токена", () ->
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().as(LoginResponseModel.class)
        );
        step("Проверка Refresh и Access токенов", () -> {
            String actualRefresh = loginResponse.refresh();
            String actualAccess = loginResponse.access();

            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).isNotEqualTo(actualAccess);
        });
    }

    @Test
    @DisplayName("Вход с невалидным password")
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);
        WrongCredentialsLoginResponseModel loginResponse = step
                ("Авторизация с невалидным полем Password ", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(wrongCredentialsLoginRequestSpec)
                                .extract().as(WrongCredentialsLoginResponseModel.class)
                );

        step("Проверка сообщения об ошибке ", () -> {
            String actualDetailError = loginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDetailError);
        });
    }

    @Test
    @DisplayName("Вход в систему с невалидным username")
    public void invalidPasswordLogin() {
        LoginBodyModel loginData = new LoginBodyModel(TestData.wrongUsername, TestData.password);
        WrongCredentialsLoginResponseModel wrongCredentialsLoginResponse = step
                ("Авторизаия с невалидным Username", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(invalidUsernameLoginRequestSpec)
                                .extract().as(WrongCredentialsLoginResponseModel.class)
                );
        step("Проверка сообщения об ошибке ", () -> {
            String actualDetailError = wrongCredentialsLoginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDataError);
        });
    }

    @Test
    @DisplayName("Вход в систему с пустыми полями username и password")
    public void emptyCredentialsLogin() {
        LoginBodyModel loginData = new LoginBodyModel("", "");
        EmptyCredentialsLoginResponseModel emptyCredentialsLoginResponse = step
                ("Авторизация с пустыми полями Username  и Password", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(emptyCredentialsLoginRequestSpec)
                                .extract().as(EmptyCredentialsLoginResponseModel.class)
                );
        step("Валидация сообщений об ошибках валидации", () -> {
            String actualUsernameError = emptyCredentialsLoginResponse.username().get(0);
            String actualPasswordError = emptyCredentialsLoginResponse.password().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
            assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }

    @Test
    @DisplayName("Вход в систему с пустым username")
    public void emptyUsernameLogin() {
        LoginBodyModel loginData = new LoginBodyModel("", TestData.password);
        EmptyUsernameLoginResponseModel emptyUsernameLoginResponse = step
                ("Авторизация с пустым полем Username", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(emptyUsernameLoginRequestSpec)
                                .extract().as(EmptyUsernameLoginResponseModel.class)
                );
        step("Проверка ошибки пустого поля Username", () -> {
            String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }

    @Test
    @DisplayName("Вход в систему с пустым password")
    public void emptyPasswordLogin() {
        LoginBodyModel loginData = new LoginBodyModel(username, "");
        EmptyPasswordLoginResponseModel emptyPasswordLoginResponse = step
                ("Авторизация с пустым Password", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(emptyPasswordLoginRequestSpec)
                                .extract().as(EmptyPasswordLoginResponseModel.class)
                );
        step("Проверка ошибки пустого поля Password", () -> {
            String actualPasswordError = emptyPasswordLoginResponse.password().get(0);
            assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }

    @Test
    @DisplayName("Вход в систему с невалидным username")
    public void invalidUsernameLogin() {
        LoginBodyModel loginData = new LoginBodyModel("", TestData.password);
        EmptyUsernameLoginResponseModel emptyUsernameLoginResponse = step
                ("Попытка авторизации с невалидными данными", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(emptyUsernameLoginRequestSpec)
                                .extract().as(EmptyUsernameLoginResponseModel.class)
                );
        step("Проверка сообщения об ошибке ", () -> {
            String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }
}