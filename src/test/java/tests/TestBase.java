package tests;

import Api.ApiClient;
import Api.AuthApiClient;
import Api.UserApiClient;
import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    protected final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUpUi() {

        RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";
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
    @AfterEach
    void afterEach() {
        closeWebDriver();
    }

}
