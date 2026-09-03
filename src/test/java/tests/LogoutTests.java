package tests;

import models.login.LoginBobyModel;
import models.logout.LogoutBodyModel;
import models.logout.SuccessfulLogoutResponseModel;
import models.logout.WrongRefreshTokenModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.successfulLoginRequestSpec;
import static specs.logout.logoutSpec.successfulLogoutResponseSpec;
import static specs.logout.logoutSpec.wrongLogoutResponseSpec;

public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Проверка успешного models.logout с использованием валидного refresh токена")
    public void successfulLogoutTest() {
        LoginBobyModel loginData = new LoginBobyModel(TestData.username, TestData.password);
        String refreshToken =
                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().path("refresh");

       LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        SuccessfulLogoutResponseModel successfulLogoutResponse =
        given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/models.logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract().as(SuccessfulLogoutResponseModel.class);

        assertThat(successfulLogoutResponse)
                .as("Ответ не null").isNotNull();
    }

    @Test
    @DisplayName("Проверка невалидного токена")
    public void wrongRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(TestData.invalidRefreshToken);
        WrongRefreshTokenModel wrongRefreshToken =
                given(baseRequestSpec)
                        .body(logoutData)
                        .when()
                        .post("/auth/models.logout/")
                        .then()
                        .spec(wrongLogoutResponseSpec)
                        .extract().as(WrongRefreshTokenModel.class);

        String actualDetailError = wrongRefreshToken.detail();
        String actualCodeError = wrongRefreshToken.code();
        assertThat(actualDetailError).isEqualTo(TestData.expectedDetailError);
        assertThat(actualCodeError).isEqualTo(TestData.expectedCodeError);
    }
}

