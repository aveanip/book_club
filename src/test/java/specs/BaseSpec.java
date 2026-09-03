package specs;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.with;

public class BaseSpec {
    public static RequestSpecification baseRequestSpec = with()
            .log().all()
            .contentType(ContentType.JSON)
            .basePath("/api/v1");
}
