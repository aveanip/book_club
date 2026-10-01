package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class RegistrationPage {
    private final SelenideElement usernameInput = $("[data-testid=username-input]");
    private final SelenideElement passwordInput = $("[data-testid=password-input]");
    private final SelenideElement confirmPasswordInput = $("[data-testid=confirm-password-input]");
    private final SelenideElement signupButton = $("[data-testid=signup-button]");
    private final SelenideElement errorMessagePassword = $("[data-testid=password-mismatch-error]");
    private final SelenideElement errorMessage = $("[data-testid=error-message]");


    @Step("Открыть страницу регистрации")
    public RegistrationPage openRegistrationPage() {
        open("/signup");
        return this;
    }

    @Step("Заполнить поле Username")
    public RegistrationPage setUsernameInput(String value) {
        usernameInput.setValue(value);
        return this;
    }

    @Step("Заполнить поле Password")
    public RegistrationPage setPasswordInput(String value) {
        passwordInput.setValue(value);
        return this;
    }

    @Step("Заполнить поле подтверждения пароля")
    public RegistrationPage setConfirmPasswordInput(String value) {
        confirmPasswordInput.setValue(value);
        return this;
    }

    @Step("Нажать на кнопку Зарегистрироваться")
    public RegistrationPage clickButton() {
        signupButton.click();
        return this;
    }

    @Step("Проверить ошибку валидации при не совпадении пароля потверждения")
    public RegistrationPage checkShowErrorWhenPasswordIsIncorrect(String value) {
        errorMessagePassword.shouldBe(visible)
                .shouldHave(text(value));
        return this;
    }

    @Step("Ошибка валидации при регистрации существующего пользователя")
    public RegistrationPage shouldShowErrorWhenRegisteringExistingUser(String value) {
        errorMessage.shouldBe(visible)
                .shouldHave(text(value));
        return this;
    }


}
