package tests;

import Api.AuthApiClient;
import Api.UserApiClient;
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
        RegistrationResponseModel registrationResponse =
                api.user.successfulUserRegistration(registrationData);
        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username1);
            assertThat(registrationResponse.id()).isGreaterThan(0);
        });

        Integer userId = registrationResponse.id();

        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, newEmail);
        PutSuccessfullUpDateUserModel successfullUpDateUser = api.user.updateUserDataPut(accessToken, userId, updateUser);

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
        RegistrationResponseModel registrationResponse = api.user.successfulUserRegistration(registrationData);

        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username1);
        });

        Integer userId = registrationResponse.id();
        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel
                ("", newFirstName, newLastName, newEmail);
        PutWrongUpDateUserModel wrongUpDateUserModel =
                api.user.updateWithEmptyFieldUsername(accessToken, userId, updateUser);

        step("Валидация сообщения об ошибке для поля username", () -> {
            String actualError = wrongUpDateUserModel.username().get(0);
            assertThat(actualError).isEqualTo(expectedErrorFieldIsEmpty);
        });
    }


    @Test
    @DisplayName("Обновление всех полей через PATCH")
    public void patchSuccessfulUpdateUserTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username1, password1);
        RegistrationResponseModel registrationResponse = api.user.successfulUserRegistration(registrationData);

        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username1);
            assertThat(registrationResponse.id()).isGreaterThan(0);
        });

        Integer userId = registrationResponse.id();
        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, newEmail);
        PutSuccessfullUpDateUserModel successfullUpDateUser =
                api.user.updateUserDataPatch(accessToken,userId,updateUser);

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
        RegistrationResponseModel registrationResponse = api.user.successfulUserRegistration(registrationData);

        step("Проверка успешной регистрации", () -> {
            assertThat(registrationResponse.username()).isEqualTo(username1);
            assertThat(registrationResponse.id()).isGreaterThan(0);
        });

        Integer userId = registrationResponse.id();

        LoginBodyModel loginData = new LoginBodyModel(username1, password1);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        UpdateBodyModel updateUser = new UpdateBodyModel(newUsername,
                newFirstName, newLastName, invalidDataEmail);
        PatchInvalidEmailModel invalidEmail = api.user.updateUserDataWithInvalidEmail(accessToken,userId,updateUser);

        step("ПОбновление поля невалидным email через PATCH", () -> {
            String actualError = invalidEmail.email().get(0);
            assertThat(actualError).isEqualTo(expectedErrorEnterValidEmailAddress);
        });
    }
}
