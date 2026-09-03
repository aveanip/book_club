package specs.registration;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

public class registrationSpec {

    public static ResponseSpecification successfulRegistrationRequestSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath("schemas.registration/registration_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("username", notNullValue())
            .expectBody("remoteAddr", notNullValue())
            .build();

    public static ResponseSpecification existingUserWrongRequestSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas.registration/existing_user_wrong_registration_schema.json"))
            .expectBody("username", notNullValue())
            .build();

    public static ResponseSpecification usernameWrongRequestSpec  = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas.registration/empty_username_response_schema.json"))
            .expectBody("username", notNullValue())
            .build();
    public static ResponseSpecification existingPasswordWrongRequestSpec  = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas.registration/empty_password_response_schema.json"))
            .expectBody("password", notNullValue())
            .build();

    public static ResponseSpecification emptyCredentialsRequestSpec  = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas.registration/ empty_credentials_response_schema.json"))
            .expectBody("username", notNullValue())
            .expectBody("password", notNullValue())
            .build();

}
