package tests;

import com.github.javafaker.Faker;
import models.login.LoginBobyModel;
import models.login.LoginResponseModel;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseModel;
import models.updateUser.PatchInvalidEmailModel;
import models.updateUser.PutSuccessfullUpDateUserModel;
import models.updateUser.PutWrongUpDateUserModel;
import models.updateUser.UpdateBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.successfulLoginRequestSpec;
import static specs.registration.registrationSpec.successfulRegistrationRequestSpec;
import static specs.updateUser.updateUserSpec.*;
import static tests.TestData.*;

public class UpdateUserTests extends TestBase {

    String username1;
    String password1;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username1 = faker.name().username();
        password1 = faker.internet().password();
    }

    @Test
    @DisplayName("Обновление всех полей через PUT")
    public void successfulUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(successfulRegistrationRequestSpec)
                        .extract().as(RegistrationResponseModel.class);

        Integer userId = registrationResponse.id();
        assertThat(registrationResponse.username()).isEqualTo(username1);

        LoginBobyModel loginData = new LoginBobyModel(username1, password1);
        LoginResponseModel loginResponse =

                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().as(LoginResponseModel.class);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, newEmail);
        PutSuccessfullUpDateUserModel successfullUpDateUser =
                given(baseRequestSpec)
                        .auth().oauth2(accessToken)
                        .body(updateUser)
                        .queryParam("id", userId)
                        .when()
                        .put("/users/me/")
                        .then()
                        .spec(successfulUpdateUserRequestSpec)
                        .extract()
                        .as(PutSuccessfullUpDateUserModel.class);

        assertThat(successfullUpDateUser.id()).isEqualTo(userId);
        assertThat(successfullUpDateUser.username()).isEqualTo(newUsername);
        assertThat(successfullUpDateUser.firstName()).isEqualTo(newFirstName);
        assertThat(successfullUpDateUser.lastName()).isEqualTo(newLastName);
        assertThat(successfullUpDateUser.email()).isEqualTo(newEmail);
        assertThat(successfullUpDateUser.remoteAddr()).isNotNull();
    }

    @Test
    @DisplayName("PUT Обновление данных с пустым username")
    public void wrongUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(successfulRegistrationRequestSpec)
                        .extract().as(RegistrationResponseModel.class);

        Integer userId = registrationResponse.id();
        assertThat(registrationResponse.username()).isEqualTo(username1);
        LoginBobyModel loginData = new LoginBobyModel(username1, password1);
        LoginResponseModel loginResponse =

                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().as(LoginResponseModel.class);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel
                ("", newFirstName, newLastName, newEmail);
        PutWrongUpDateUserModel wrongUpDateUserModel =
                given(baseRequestSpec)
                        .auth().oauth2(accessToken)
                        .body(updateUser)
                        .queryParam("id", userId)
                        .when()
                        .put("/users/me/")
                        .then()
                        .spec(wrongUpdateUserRequestSpec)
                        .extract().as(PutWrongUpDateUserModel.class);

        String actualError = wrongUpDateUserModel.username().get(0);
        assertThat(actualError).isEqualTo(expectedErrorFieldIsEmpty);
    }


    @Test
    @DisplayName("Обновление всех полей через PATCH")
    public void patchSuccessfulUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(successfulRegistrationRequestSpec)
                        .extract().as(RegistrationResponseModel.class);

        Integer userId = registrationResponse.id();
        assertThat(registrationResponse.username()).isEqualTo(username1);

        LoginBobyModel loginData = new LoginBobyModel(username1, password1);
        LoginResponseModel loginResponse =

                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().as(LoginResponseModel.class);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, newEmail);
        PutSuccessfullUpDateUserModel successfullUpDateUser =
                given(baseRequestSpec)
                        .auth().oauth2(accessToken)
                        .body(updateUser)
                        .queryParam("id", userId)
                        .when()
                        .patch("/users/me/")
                        .then()
                        .spec(patchSuccessfulUpdateUserRequestSpec )
                        .extract()
                        .as(PutSuccessfullUpDateUserModel.class);

        assertThat(successfullUpDateUser.id()).isEqualTo(userId);
        assertThat(successfullUpDateUser.username()).isEqualTo(newUsername);
        assertThat(successfullUpDateUser.firstName()).isEqualTo(newFirstName);
        assertThat(successfullUpDateUser.lastName()).isEqualTo(newLastName);
        assertThat(successfullUpDateUser.email()).isEqualTo(newEmail);
        assertThat(successfullUpDateUser.remoteAddr()).isNotNull();
    }

    @Test
    @DisplayName("Обновление поля невалидным email PATCH")
    public void patchInvalidEmailTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse =
                given(baseRequestSpec)
                        .body(registrationData)
                        .when()
                        .post("/users/register/")
                        .then()
                        .spec(successfulRegistrationRequestSpec)
                        .extract().as(RegistrationResponseModel.class);

        Integer userId = registrationResponse.id();
        assertThat(registrationResponse.username()).isEqualTo(username1);

        LoginBobyModel loginData = new LoginBobyModel(username1, password1);
        LoginResponseModel loginResponse =

                given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(successfulLoginRequestSpec)
                        .extract().as(LoginResponseModel.class);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, invalidEmail);
        PatchInvalidEmailModel invalidEmail =
                given(baseRequestSpec)
                        .auth().oauth2(accessToken)
                        .body(updateUser)
                        .queryParam("id", userId)
                        .when()
                        .patch("/users/me/")
                        .then()
                        .spec(patchInvalidEmailRequestSpec)
                        .extract()
                        .as(PatchInvalidEmailModel.class);

        String actualError = invalidEmail.email().get(0);
        assertThat(actualError).isEqualTo(expectedErrorEnterValidEmailAddress);

    }


}
