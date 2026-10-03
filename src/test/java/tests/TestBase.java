package tests;

import Api.ApiClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    protected final ApiClient api = new ApiClient();

    @BeforeAll
    public static void setUpUi() {

        RestAssured.baseURI = "https://book-club.qa.guru";
        RestAssured.basePath = "/api/v1";
    }
}

