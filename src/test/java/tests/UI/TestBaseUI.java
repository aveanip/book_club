package tests.UI;

import Api.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBaseUI {


    protected final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUpUi() {
        Configuration.baseUrl = System.getProperty("baseUrl");
        Configuration.browser = System.getProperty("BROWSER");
        Configuration.browserSize = System.getProperty("BROWSER_SIZE");
        Configuration.browserVersion = System.getProperty("BROWSER_VERSION");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("HEADLESS", "false"));

        String selenoidUrl= System.getProperty("SELENOID_URL");
        if (selenoidUrl == null || selenoidUrl.isEmpty() || "null".equals(selenoidUrl)) {
            selenoidUrl = "https://user1:1234@selenoid.autotests.cloud/wd/hub";

        }
        Configuration.remote = selenoidUrl;
        DesiredCapabilities capabilities = new DesiredCapabilities();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(List.of("--disable-dev-shm-usage", "--no-sandbox"));
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                "enableVNC", true,
                "enableVideo", true
        ));
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

