package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;


public class LoginTests extends TestBase {
    @Test
    @DisplayName("Успешная авторизация с валидными данными")
    public void successfulLoginTest() {
        LoginBobyModel loginData = new LoginBobyModel(username, password);
        LoginResponseModel loginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().as(LoginResponseModel.class);

        String actualRefresh = loginResponse.refresh();
        String actualAccess = loginResponse.access();

        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).isNotEqualTo(actualAccess);
    }

    @Test
    @DisplayName("Вход с невалидным password")
    public void wrongCredentialsLoginTest() {
        LoginBobyModel loginData = new LoginBobyModel(username, wrongPassword);
        String expectedDetailError = "Invalid username or password.";

        WrongCredentialsLoginResponseModel loginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(wrongCredentialsLoginRequestSpec)
                        .extract().as(WrongCredentialsLoginResponseModel.class);

        String actualDetailError = loginResponse.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    @DisplayName("Вход в систему с невалидным usernama")
    public void invalidPasswordLogin() {
        LoginBobyModel loginData = new LoginBobyModel(TestData.wrongUsername, TestData.password);
        WrongCredentialsLoginResponseModel wrongCredentialsLoginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(invalidUsernameLoginRequestSpec)
                        .extract().as(WrongCredentialsLoginResponseModel.class);

        String actualDetailError = wrongCredentialsLoginResponse.detail();
        assertThat(actualDetailError).isEqualTo(expectedDataError);
    }

    @Test
    @DisplayName("Вход в систему с пустыми полями username и password")
    public void emptyCredentialsLogin() {
        LoginBobyModel loginData = new LoginBobyModel("", "");
        EmptyCredentialsLoginResponseModel emptyCredentialsLoginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(emptyCredentialsLoginRequestSpec)
                        .extract().as(EmptyCredentialsLoginResponseModel.class);

        String actualUsernameError = emptyCredentialsLoginResponse.username().get(0);
        String actualPasswordError = emptyCredentialsLoginResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
        assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
    }

    @Test
    @DisplayName("Вход в систему с пустым username")
    public void emptyUsernameLogin() {
        LoginBobyModel loginData = new LoginBobyModel("", TestData.password);
        EmptyUsernameLoginResponseModel emptyUsernameLoginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(emptyUsernameLoginRequestSpec)
                        .extract().as(EmptyUsernameLoginResponseModel.class);

        String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);

    }

    @Test
    @DisplayName("Вход в систему с пустым password")
    public void emptyPasswordLogin() {
        LoginBobyModel loginData = new LoginBobyModel(username, "");
        EmptyPasswordLoginResponseModel emptyPasswordLoginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(emptyPasswordLoginRequestSpec)
                        .extract().as(EmptyPasswordLoginResponseModel.class);

        String actualPasswordError = emptyPasswordLoginResponse.password().get(0);
        assertThat(actualPasswordError).isEqualTo(expectedErrorFieldIsEmpty);
    }

    @Test
    @DisplayName("Вход в систему с невалидным username")
    public void invalidUsernameLogin() {
        LoginBobyModel loginData = new LoginBobyModel("", TestData.password);
        EmptyUsernameLoginResponseModel emptyUsernameLoginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(emptyUsernameLoginRequestSpec)
                        .extract().as(EmptyUsernameLoginResponseModel.class);

        String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedErrorFieldIsEmpty);
    }
}