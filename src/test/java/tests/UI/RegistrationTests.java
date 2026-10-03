package tests.UI;

import com.github.javafaker.Faker;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ClubPage;
import pages.RegistrationPage;
import tests.TestBase;
import java.util.Locale;
import static io.qameta.allure.Allure.step;

public class RegistrationTests extends TestBase {

    Faker faker = new Faker(new Locale("en"));
    String password = "12345";
    String invalidPassword = "123456";
    String uniqueId = String.valueOf(System.currentTimeMillis()).substring(7);
    String uniqueUsername = faker.name().username() + "_" + uniqueId;;

    ClubPage clubPage = new ClubPage();
    RegistrationPage registrationPage = new RegistrationPage();


    @Test
    @DisplayName("[UI] Успешная регистрация и проверка отображения на панель клубов")
    public void successfulRegistrationTest() {
        step("[UI] Заполнить форму и успешно зарегистрироваться", () -> {
            registrationPage
                    .openRegistrationPage()
                    .setUsernameInput(uniqueUsername)
                    .setPasswordInput(password)
                    .setConfirmPasswordInput(password)
                    .clickButton();
        });
        step("[UI] Проверить, что пользователь видит панель клубов", () -> {
            clubPage
                    .verifyClubsPanelVisible()
                    .checkButtonBlock("Все клубы", "Мои клубы", "Участвую");
        });
    }

    @Test
    @DisplayName("Ошибка при попытке зарегистрировать уже существующего пользователя")
    public void checkShowErrorWhenUserAlreadyExistsTest() {

        step("[API] Регистрация валидного пользователя через API", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(uniqueUsername, password);
            api.user.successfulUserRegistration(registrationData);
        });
        step("[UI] Повторная регистрация пользователя", () -> {
            registrationPage
                    .openRegistrationPage()
                    .setUsernameInput(uniqueUsername)
                    .setPasswordInput(password)
                    .setConfirmPasswordInput(password)
                    .clickButton();
        });
        step("[UI Проверка валидации для уже зарегистрированого пользователя] ", () -> {
            registrationPage
                    .shouldShowErrorWhenRegisteringExistingUser("Ошибка при регистрации");
        });
    }


    @Test
    @DisplayName("[UI] Ошибка валидации: проверка сообщения при несовпадении паролей")
    public void ExistsTest() {
        step("Заполнить форму с несовпадающими паролями и нажать кнопку Зарегистрироваться", () -> {
            registrationPage
                    .openRegistrationPage()
                    .setUsernameInput(uniqueUsername)
                    .setPasswordInput(password)
                    .setConfirmPasswordInput(invalidPassword)
                    .clickButton();
        });
        step("[UI] Система показывает ошибку о несовпадении паролей", () -> {
            registrationPage
                    .checkShowErrorWhenPasswordIsIncorrect("Пароли не совпадают");
        });
    }
}
