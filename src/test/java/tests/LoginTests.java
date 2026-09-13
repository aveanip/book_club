package tests;

import Api.AuthApiClient;
import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;


public class LoginTests extends TestBase {

    @Test
    @DisplayName("Успешная авторизация с валидными данными")
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

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
        WrongCredentialsLoginResponseModel loginResponse =  api.auth.invalidCredentialsPassword(loginData);

        step("Проверка сообщения об ошибке ", () -> {
            String actualDetailError = loginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDataError);
        });
    }

    @Test
    @DisplayName("Вход в систему с невалидным username")
    public void invalidPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(wrongUsername, password);
        WrongCredentialsLoginResponseModel wrongCredentialsLoginResponse =
                api.auth.wrongCredentialsUsername(loginData);
        step("Проверка сообщения об ошибке ", () -> {
            String actualDetailError = wrongCredentialsLoginResponse.detail();
            assertThat(actualDetailError).isEqualTo(expectedDataError);
        });
    }

    @Test
    @DisplayName("Вход в систему с пустыми полями username и password")
    public void emptyCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", "");
        EmptyCredentialsLoginResponseModel emptyCredentialsLoginResponse =
                api.auth.emptyCredentialsLogin(loginData);
        step("Валидация сообщений об ошибках валидации", () -> {
            String actualUsernameError = emptyCredentialsLoginResponse.username().get(0);
            String actualPasswordError = emptyCredentialsLoginResponse.password().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
            assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }

    @Test
    @DisplayName("Вход в систему с пустым username")
    public void emptyUsernameLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", TestData.password);
        EmptyUsernameLoginResponseModel emptyUsernameLoginResponse =  api.auth.emptyCredentialsUsername(loginData);
        step("Проверка ошибки пустого поля Username", () -> {
            String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }

    @Test
    @DisplayName("Вход в систему с пустым password")
    public void emptyPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(username, "");
        EmptyPasswordLoginResponseModel emptyPasswordLoginResponse =  api.auth.emptyCredentialsPassword(loginData);
        step("Проверка ошибки пустого поля Password", () -> {
            String actualPasswordError = emptyPasswordLoginResponse.password().get(0);
            assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }

    @Test
    @DisplayName("Вход в систему с невалидным username")
    public void invalidUsernameLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(wrongUsername, password);
        EmptyUsernameLoginResponseModel emptyUsernameLoginResponse =
                api.auth.invalidCredentialsUsername(loginData);
        step("Проверка сообщения об ошибке ", () -> {
            String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
            assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }
}