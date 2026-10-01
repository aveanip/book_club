package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    private final SelenideElement inputUsername = $("[data-testid=username-input]");
    private final SelenideElement inputPassword = $("[data-testid=password-input]");
    private final SelenideElement submitButton = $("[data-testid=submit-button]");



    private final SelenideElement errorText = $("[data-testid=error-message]");


    @Step("Открыть страницу авторизации")
    public LoginPage openLoginPage() {
        open("/signin");
        return this;
    }

    @Step("Заполнить поле username")
    public LoginPage setUsername(String value) {
        inputUsername.setValue(value);
        return this;
    }

    @Step("Заполнить поле Password")
    public LoginPage setPassword(String value) {
        inputPassword.setValue(value);
        return this;
    }

    @Step("Нажать на кнопку Войти")
    public LoginPage clickButton() {
        submitButton.click();
        return this;
    }

    @Step("Проверить текст валидации ошибки.")
    public LoginPage chekErrorText(String value) {
        errorText.shouldBe(visible)
                .shouldHave(text(value));
return this;
    }
}


