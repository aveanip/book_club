package tests;

import Api.AuthApiClient;
import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.SuccessfulLogoutResponseModel;
import models.logout.WrongRefreshTokenModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.authentication;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.successfulLoginRequestSpec;
import static specs.logout.logoutSpec.successfulLogoutResponseSpec;
import static specs.logout.logoutSpec.wrongLogoutResponseSpec;

public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный logout по валидному refresh токену")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(TestData.username, TestData.password);
        String refreshToken = authApiClient.loginAndGetRefreshToken(loginData);
        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        authApiClient.logout(logoutData);

        step("Проверка успешного logout", () -> {
            assertThat(logoutData).isNotNull();
        });
    }

    @Test
    @DisplayName("Проверка невалидного токена")
    public void wrongRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(TestData.invalidRefreshToken);
        WrongRefreshTokenModel wrongRefreshToken = authApiClient.wrongRefreshToken(logoutData);

        step("Проверка сообщения об ошибке", () -> {
            String actualDetailError = wrongRefreshToken.detail();
            String actualCodeError = wrongRefreshToken.code();
            assertThat(actualDetailError).isEqualTo(TestData.expectedDetailError);
            assertThat(actualCodeError).isEqualTo(TestData.expectedCodeError);
        });
    }
}

