package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ClubPage {
    private final SelenideElement сlubsSearchPanel = $(".filter-options");
    private final SelenideElement clubContent = $(".club-content");
    private final SelenideElement leaveBtn = $(".leave-btn");
    private final SelenideElement error = $(".error");


    @Step("Открыть страницу создания клуба")
    public ClubPage openClubCreationPage() {
        open("/clubs/create");
        return this;
    }

    @Step("Открыть простую страницу")
    public ClubPage openPage() {
        open("/favicon.ico");
        return this;
    }

    @Step("Открыть страницу с инфо о клубе")
    public ClubPage openClubInfoPage(int clubId) {
        open("/clubs/" + clubId);
        return this;
    }

    @Step("Проверка видимости информации о клубе")
    public ClubPage checkClubInfo() {
        clubContent.shouldBe(visible);
        return this;
    }

    @Step("Нажатие на кнопку Покинуть клуб")
    public ClubPage clickLeaveBtn() {
        leaveBtn.click();
        return this;
    }


    @Step("Проверка видимости ошибки о невозможности покинуть клуб")
    public ClubPage checkTextClubError(String value) {
        error.shouldBe(visible)
                .shouldHave(text(value));
        return this;
    }

    @Step("Проверка видимости подтверждения выхода из клуба")
    public ClubPage confirmLeaveClub() {
        confirm();
        return this;
    }

    @Step("Вставить в Local Storage JSON авторизации")
    public ClubPage putAuthIntoLocalStorage(String localStorageAuthBody) {
        localStorage().setItem("book_club_auth", localStorageAuthBody);
        return this;
    }

    @Step("После успешной авторизации пользователь видит Навигационную панель клубов")
    public ClubPage verifyClubsPanelVisible() {
        сlubsSearchPanel.shouldBe(visible);
        return this;
    }

    @Step("Проверить кнопки в панеле фильтров")
    public ClubPage checkButtonBlock(String value, String value2, String value3) {
        сlubsSearchPanel.shouldHave(text(value)).shouldHave(visible)
                .shouldHave(text(value2)).shouldHave(visible)
                .shouldHave(text(value3)).shouldHave(visible);
        return this;
    }


}























