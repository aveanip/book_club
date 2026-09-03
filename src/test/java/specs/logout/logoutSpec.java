package specs.logout;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class logoutSpec {

    public static ResponseSpecification successfulLogoutResponseSpec =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .build();

    public static ResponseSpecification wrongLogoutResponseSpec =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas.logout/wrong_refresh_token_schema.json"))
            .expectBody("detail", notNullValue())
            .expectBody("code", notNullValue())
            .build();


}
