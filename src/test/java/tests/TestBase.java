package tests;

import Api.AuthApiClient;
import Api.UserApiClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    public final UserApiClient userApiClient = new UserApiClient();
    public final AuthApiClient authApiClient = new AuthApiClient();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://book-club.qa.guru";
    }

}
