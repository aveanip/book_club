package tests;

import com.github.javafaker.Faker;
import models.login.LoginBodyModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseModel;
import models.updateUser.PatchInvalidEmailModel;
import models.updateUser.PutSuccessfullUpDateUserModel;
import models.updateUser.PutWrongUpDateUserModel;
import models.updateUser.UpdateBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
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
        RegistrationResponseModel registrationResponse = step
                ("Регистрация нового пользователя", () ->
                        given(baseRequestSpec)
                                .body(registrationData)
                                .when()
                                .post("/users/register/")
                                .then()
                                .spec(successfulRegistrationRequestSpec)
                                .extract().as(RegistrationResponseModel.class)
                );
        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username1);
            assertThat(registrationResponse.id()).isGreaterThan(0);
        });

        Integer userId = registrationResponse.id();

        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = step(
                "Авторизация пользователя и получение access токена", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(successfulLoginRequestSpec)
                                .extract().as(LoginResponseModel.class)
        );

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, newEmail);
        PutSuccessfullUpDateUserModel successfullUpDateUser = step(
                "Обновление всех полей пользователя через PUT-запрос", () ->
                        given(baseRequestSpec)
                                .auth().oauth2(accessToken)
                                .body(updateUser)
                                .queryParam("id", userId)
                                .when()
                                .put("/users/me/")
                                .then()
                                .spec(successfulUpdateUserRequestSpec)
                                .extract()
                                .as(PutSuccessfullUpDateUserModel.class)
        );

        step("Валидация обновленных данных пользователя", () -> {
            assertThat(successfullUpDateUser.id()).isEqualTo(userId);
            assertThat(successfullUpDateUser.username()).isEqualTo(newUsername);
            assertThat(successfullUpDateUser.firstName()).isEqualTo(newFirstName);
            assertThat(successfullUpDateUser.lastName()).isEqualTo(newLastName);
            assertThat(successfullUpDateUser.email()).isEqualTo(newEmail);
            assertThat(successfullUpDateUser.remoteAddr()).isNotNull();
        });
    }

    @Test
    @DisplayName("PUT Обновление данных с пустым username")
    public void wrongUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse = step
                ("Регистрация нового пользователя", () ->
                        given(baseRequestSpec)
                                .body(registrationData)
                                .when()
                                .post("/users/register/")
                                .then()
                                .spec(successfulRegistrationRequestSpec)
                                .extract().as(RegistrationResponseModel.class)
                );

        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username1);
        });

        Integer userId = registrationResponse.id();
        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = step(
                "Авторизация пользователя и получение access токена", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(successfulLoginRequestSpec)
                                .extract().as(LoginResponseModel.class)
        );

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel
                ("", newFirstName, newLastName, newEmail);
        PutWrongUpDateUserModel wrongUpDateUserModel = step(
                "Попытка обновления пользователя с пустым username", () ->
                        given(baseRequestSpec)
                                .auth().oauth2(accessToken)
                                .body(updateUser)
                                .queryParam("id", userId)
                                .when()
                                .put("/users/me/")
                                .then()
                                .spec(wrongUpdateUserRequestSpec)
                                .extract().as(PutWrongUpDateUserModel.class)
        );

        step("Валидация сообщения об ошибке для поля username", () -> {
            String actualError = wrongUpDateUserModel.username().get(0);
            assertThat(actualError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }


    @Test
    @DisplayName("Обновление всех полей через PATCH")
    public void patchSuccessfulUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse = step
                ("Регистрация нового пользователя", () ->
                        given(baseRequestSpec)
                                .body(registrationData)
                                .when()
                                .post("/users/register/")
                                .then()
                                .spec(successfulRegistrationRequestSpec)
                                .extract().as(RegistrationResponseModel.class)
                );

        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username1);
            assertThat(registrationResponse.id()).isGreaterThan(0);
        });

        Integer userId = registrationResponse.id();
        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = step
                ("Авторизация пользователя и получение access токена", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(successfulLoginRequestSpec)
                                .extract().as(LoginResponseModel.class)
                );

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, newEmail);
        PutSuccessfullUpDateUserModel successfullUpDateUser = step
                ("Обновление всех полей пользователя через PATCH-запрос", () ->
                        given(baseRequestSpec)
                                .auth().oauth2(accessToken)
                                .body(updateUser)
                                .queryParam("id", userId)
                                .when()
                                .patch("/users/me/")
                                .then()
                                .spec(patchSuccessfulUpdateUserRequestSpec)
                                .extract()
                                .as(PutSuccessfullUpDateUserModel.class)
                );

        step("Валидация обновленных данных пользователя", () -> {
            assertThat(successfullUpDateUser.id()).isEqualTo(userId);
            assertThat(successfullUpDateUser.username()).isEqualTo(newUsername);
            assertThat(successfullUpDateUser.firstName()).isEqualTo(newFirstName);
            assertThat(successfullUpDateUser.lastName()).isEqualTo(newLastName);
            assertThat(successfullUpDateUser.email()).isEqualTo(newEmail);
            assertThat(successfullUpDateUser.remoteAddr()).isNotNull();
        });
    }

    @Test
    @DisplayName("Обновление поля невалидным email PATCH")
    public void patchInvalidEmailTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse = step
                ("Регистрация нового пользователя", () ->
                        given(baseRequestSpec)
                                .body(registrationData)
                                .when()
                                .post("/users/register/")
                                .then()
                                .spec(successfulRegistrationRequestSpec)
                                .extract().as(RegistrationResponseModel.class)
                );
        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username1);
            assertThat(registrationResponse.id()).isGreaterThan(0);
        });

        Integer userId = registrationResponse.id();

        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = step
                ("Авторизация пользователя и получение access токена", () ->
                        given(baseRequestSpec)
                                .body(loginData)
                                .when()
                                .post("/auth/token/")
                                .then()
                                .spec(successfulLoginRequestSpec)
                                .extract().as(LoginResponseModel.class)
                );

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, invalidDataEmail);
        PatchInvalidEmailModel invalidEmail = step
                ("Попытка обновить данные пользователя невалидным email", () ->
                        given(baseRequestSpec)
                                .auth().oauth2(accessToken)
                                .body(updateUser)
                                .queryParam("id", userId)
                                .when()
                                .patch("/users/me/")
                                .then()
                                .spec(patchInvalidEmailRequestSpec)
                                .extract()
                                .as(PatchInvalidEmailModel.class)
                );

        step("ПОбновление поля невалидным email через PATCH", () -> {
            String actualError = invalidEmail.email().get(0);
            assertThat(actualError).isEqualTo(expectedErrorEnterValidEmailAddress);
        });
    }
}
