package tests.UI;

import com.github.javafaker.Faker;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ClubPage;
import pages.LoginPage;

import java.util.Locale;

import static io.qameta.allure.Allure.step;


public class LoginUiTest extends TestBaseUI {

    Faker faker = new Faker(new Locale("en"));
    String uniqueId = String.valueOf(System.currentTimeMillis()).substring(7);
    String password = "12345";
    String uniqueUsername = faker.name().username() + "_" + uniqueId;;
    String invalidUsername = faker.name().username();

    LoginPage loginPage = new LoginPage();
    ClubPage clubPage = new ClubPage();

    @Test
    @DisplayName("Ошибка авторизации при вводе неверного логина")
    public void loginWithInvalidUsernameShouldShowErrorTest() {
        step("[API] Регистрация валидного пользователя через API", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(uniqueUsername, password);
            api.user.successfulUserRegistration(registrationData);
        });
        step("[UI] Попытаться войти с невалидным логином", () -> {
            loginPage
                    .openLoginPage()
                    .setUsername(invalidUsername)
                    .setPassword(password)
                    .clickButton();
        });
        step("[UI] Проверка: отображение текста ошибки \"Ты не пройдешь!\"", () -> {
            loginPage
                    .clickButton()
                    .chekErrorText("Ты не пройдешь!");
        });
        }

        @Test
        @DisplayName("[UI] Успешная авторизация и отображение панели клубов")
        public void successfulLoginShouldShowClubsPanelTest () {
            step("[API] Регистрация валидного пользователя через API", () -> {
                        RegistrationBodyModel registrationData = new RegistrationBodyModel(uniqueUsername, password);
                        api.user.successfulUserRegistration(registrationData);
                    });
            step("[UI] Вход в систему с валидными данными", () -> {
                        loginPage
                                .openLoginPage()
                                .setUsername(uniqueUsername)
                                .setPassword(password)
                                .clickButton();
                    });
                step("[UI] Панель клубов и кнопки фильтрации отображаются", () -> {
                    clubPage
                            .verifyClubsPanelVisible()
                            .checkButtonBlock("Все клубы", "Мои клубы", "Участвую");
                });
            }
    }


