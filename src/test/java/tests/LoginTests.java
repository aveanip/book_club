package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;

public class LoginTests extends TestBase {
    @Test
    @DisplayName("Успешная авторизация с валидными данными")
    public void successfulLoginTest() {
        LoginBobyModel loginData = new LoginBobyModel(TestData.username, TestData.password);
        LoginResponseModel loginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().as(LoginResponseModel.class);

        String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String actualRefresh = loginResponse.refresh();
        String actualAccess = loginResponse.access();

        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).isNotEqualTo(actualAccess);
    }

    @Test
    @DisplayName("Вход с невалидным password")
    public void wrongCredentialsLoginTest() {
        LoginBobyModel loginData = new LoginBobyModel(TestData.username, TestData.wrongPassword);
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
        String expectedDetailError = "Invalid username or password.";
        WrongCredentialsLoginResponseModel wrongCredentialsLoginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(invalidUsernameLoginRequestSpec)
                        .extract().as(WrongCredentialsLoginResponseModel.class);

        String actualDetailError = wrongCredentialsLoginResponse.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
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

        String expectedUsernameError = "This field may not be blank.";
        String expectedPasswordError = "This field may not be blank.";
        String actualUsernameError = emptyCredentialsLoginResponse.username().get(0);
        String actualPasswordError = emptyCredentialsLoginResponse.password().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
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

        String expectedUsernameError = "This field may not be blank.";
        String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);

    }

    @Test
    @DisplayName("Вход в систему с пустым password")
    public void emptyPasswordLogin() {
        LoginBobyModel loginData = new LoginBobyModel(TestData.username, "");
        EmptyPasswordLoginResponseModel emptyPasswordLoginResponse =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(emptyPasswordLoginRequestSpec)
                        .extract().as(EmptyPasswordLoginResponseModel.class);

        String expectedPasswordError = "This field may not be blank.";
        String actualPasswordError = emptyPasswordLoginResponse.password().get(0);
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
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

        String expectedUsernameError = "This field may not be blank.";
        String actualUsernameError = emptyUsernameLoginResponse.username().get(0);
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
    }
}