package Api;

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

    public LoginResponseModel login(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginRequestSpec)
                .extract().as(LoginResponseModel.class);
    }

    public WrongCredentialsLoginResponseModel invalidCredentialsPassword(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginRequestSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);
    }

    public WrongCredentialsLoginResponseModel wrongCredentialsUsername(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(invalidUsernameLoginRequestSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);
    }

    public EmptyCredentialsLoginResponseModel emptyCredentialsLogin(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyCredentialsLoginRequestSpec)
                .extract().as(EmptyCredentialsLoginResponseModel.class);
    }

    public EmptyUsernameLoginResponseModel emptyCredentialsUsername(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameLoginRequestSpec)
                .extract().as(EmptyUsernameLoginResponseModel.class);
    }

    public EmptyPasswordLoginResponseModel emptyCredentialsPassword(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginRequestSpec)
                .extract().as(EmptyPasswordLoginResponseModel.class);
    }

    public EmptyUsernameLoginResponseModel invalidCredentialsUsername(LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyUsernameLoginRequestSpec)
                .extract().as(EmptyUsernameLoginResponseModel.class);
    }

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

   public SuccessfulLogoutResponseModel logout (LogoutBodyModel logoutData){
                return given(baseRequestSpec)
                        .body(logoutData)
                        .when()
                        .post("/auth/logout/")
                        .then()
                        .spec(successfulLogoutResponseSpec)
                        .extract().as(SuccessfulLogoutResponseModel.class);
            }
            public WrongRefreshTokenModel wrongRefreshToken (LogoutBodyModel logoutData) {
                return given(baseRequestSpec)
                        .body(logoutData)
                        .when()
                        .post("/auth/logout/")
                        .then()
                        .spec(wrongLogoutResponseSpec)
                        .extract().as(WrongRefreshTokenModel.class);
            }
}
