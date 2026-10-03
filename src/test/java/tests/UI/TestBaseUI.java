package tests.UI;

import Api.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBaseUI {


    protected final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUpUi() {
        Configuration.baseUrl = System.getProperty("baseUrl");
        Configuration.browser = System.getProperty("browser");
        Configuration.browserSize = System.getProperty("browserSize");
        Configuration.browserVersion = System.getProperty("browserVersion");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        String selenoidUrl= System.getProperty("selenoidUrl");
        if (selenoidUrl == null || selenoidUrl.isEmpty() || "null".equals(selenoidUrl)) {
            selenoidUrl = "https://user1:1234@selenoid.autotests.cloud/wd/hub";

        }
        Configuration.remote = selenoidUrl;
    }
    @BeforeEach
    public void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    @AfterEach
    void afterEach() {
        closeWebDriver();
    }
    @AfterEach
    void addAttachments(){
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
//        Attach.attachAsText("Some file", "Some content");
        Attach.browserConsoleLogs();
        Attach.addVideo();
        Attach.getVideoUrl();
    }
}

