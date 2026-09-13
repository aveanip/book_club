package Api;

import io.qameta.allure.Step;
import models.registration.*;
import models.updateUser.PatchInvalidEmailModel;
import models.updateUser.PutSuccessfullUpDateUserModel;
import models.updateUser.PutWrongUpDateUserModel;
import models.updateUser.UpdateBodyModel;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.registrationSpec.*;
import static specs.updateUser.updateUserSpec.*;

public class UserApiClient {

    @Step("Успешная регистрация нового пользователя")
    public RegistrationResponseModel successfulUserRegistration(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationRequestSpec)
                .extract().as(RegistrationResponseModel.class);
    }

    @Step("Попытка повторной регистрации уже существующего пользователя")
    public ExistingUserResponseModel secondregistrationResponse(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserWrongRequestSpec)
                .extract().as(ExistingUserResponseModel.class);
    }

    @Step("Попытка регистрации без поля username")
    public ExistingUserResponseModel registrationWithoutFieldUsername(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(usernameWrongRequestSpec)
                .extract().as(ExistingUserResponseModel.class);
    }

    @Step("Попытка регистрации без поля password")
    public EmptyPasswordResponseModel registrationWithoutFieldPassword(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingPasswordWrongRequestSpec)
                .extract().as(EmptyPasswordResponseModel.class);
    }

    @Step("Попытка регистрации с пустыми логином и паролем")
    public EmptyCredentialsResponseModel emptyCredentialsUsernameAndPassword(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyCredentialsRequestSpec)
                .extract().as(EmptyCredentialsResponseModel.class);
    }

    @Step("Полное обновление данных пользователя (PUT)")
    public PutSuccessfullUpDateUserModel updateUserDataPut(String accessToken, Integer userId, UpdateBodyModel updateUser) {
        return given(baseRequestSpec)
                .auth().oauth2(accessToken)
                .body(updateUser)
                .queryParam("id", userId)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUpdateUserRequestSpec)
                .extract()
                .as(PutSuccessfullUpDateUserModel.class);
    }

    @Step("Попытка обновления данных с пустым полем username (PUT)")
    public PutWrongUpDateUserModel updateWithEmptyFieldUsername(String accessToken, Integer userId, UpdateBodyModel updateUser) {
        return given(baseRequestSpec)
                .auth().oauth2(accessToken)
                .body(updateUser)
                .queryParam("id", userId)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongUpdateUserRequestSpec)
                .extract().as(PutWrongUpDateUserModel.class);
    }

    @Step("Частичное обновление данных пользователя (PATCH)")
    public PutSuccessfullUpDateUserModel updateUserDataPatch(String accessToken, Integer userId, UpdateBodyModel updateUser) {
        return given(baseRequestSpec)
                .auth().oauth2(accessToken)
                .body(updateUser)
                .queryParam("id", userId)
                .when()
                .patch("/users/me/")
                .then()
                .spec(patchSuccessfulUpdateUserRequestSpec)
                .extract()
                .as(PutSuccessfullUpDateUserModel.class);

    }

    @Step("Попытка частичного обновления с невалидным email (PATCH)")
    public PatchInvalidEmailModel updateUserDataWithInvalidEmail(String accessToken, Integer userId, UpdateBodyModel updateUser) {
       return given(baseRequestSpec)
                .auth().oauth2(accessToken)
                .body(updateUser)
                .queryParam("id", userId)
                .when()
                .patch("/users/me/")
                .then()
                .spec(patchInvalidEmailRequestSpec)
                .extract()
                .as(PatchInvalidEmailModel.class);
    }
}
