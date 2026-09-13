package Api;

import io.qameta.allure.Step;
import models.login.*;
import models.logout.LogoutBodyModel;
import models.logout.SuccessfulLogoutResponseModel;
import models.logout.WrongRefreshTokenModel;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static specs.logout.logoutSpec.successfulLogoutResponseSpec;
import static specs.logout.logoutSpec.wrongLogoutResponseSpec;

public class AuthApiClient {

    @Step("Авторизация и получение токена")
    public LoginResponseModel login(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginRequestSpec)
                .extract().as(LoginResponseModel.class);
    }

    @Step("Попытка авторизации с неверным паролем")
    public WrongCredentialsLoginResponseModel invalidCredentialsPassword(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginRequestSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Попытка авторизации с неверным Username")
    public WrongCredentialsLoginResponseModel wrongCredentialsUsername(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(invalidUsernameLoginRequestSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Попытка авторизации с пустым логином и паролем ")
    public EmptyCredentialsLoginResponseModel emptyCredentialsLogin(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyCredentialsLoginRequestSpec)
                .extract().as(EmptyCredentialsLoginResponseModel.class);
    }

    @Step("Попытка авторизации с пустым Username")
    public EmptyUsernameLoginResponseModel emptyCredentialsUsername(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameLoginRequestSpec)
                .extract().as(EmptyUsernameLoginResponseModel.class);
    }

    @Step("Попытка авторизации с пустым Password")
    public EmptyPasswordLoginResponseModel emptyCredentialsPassword(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginRequestSpec)
                .extract().as(EmptyPasswordLoginResponseModel.class);
    }

    @Step("Попытка авторизации с невалидным логином")
    public EmptyUsernameLoginResponseModel invalidCredentialsUsername(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameLoginRequestSpec)
                .extract().as(EmptyUsernameLoginResponseModel.class);
    }

    @Step("Авторизация и получение refresh-токена")
    public String loginAndGetRefreshToken(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginRequestSpec)
                .extract()
                .path("refresh");
    }

    @Step("Выход из системы (logout)")
    public SuccessfulLogoutResponseModel logout(LogoutBodyModel logoutData) {
        return given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract().as(SuccessfulLogoutResponseModel.class);
    }

    @Step("Попытка выхода из системы с невалидным refresh-токеном")
    public WrongRefreshTokenModel wrongRefreshToken(LogoutBodyModel logoutData) {
        return given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(wrongLogoutResponseSpec)
                .extract().as(WrongRefreshTokenModel.class);
    }
}
