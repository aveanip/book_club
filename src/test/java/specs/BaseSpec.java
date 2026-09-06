package specs;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import static allure.CustomAllureListener.withCustomTemplate;
import static io.restassured.RestAssured.with;

public class BaseSpec {
    public static RequestSpecification baseRequestSpec = with()
            .filter(withCustomTemplate())
            .log().all()
            .contentType(ContentType.JSON)
            .basePath("/api/v1");
}
