package specs.updateUser;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static tests.TestData.username;

public class updateUserSpec {

    public static ResponseSpecification successfulUpdateUserRequestSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody("id", notNullValue())
            .expectBody("username", notNullValue())
            .expectBody("firstName", notNullValue())
            .expectBody("email", notNullValue())
            .expectBody("remoteAddr", notNullValue())
            .expectBody(matchesJsonSchemaInClasspath
                    ("schemas.updateUser/update_user_schema.json"))
            .build();

    public static ResponseSpecification wrongUpdateUserRequestSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody("username[0]", equalTo("This field may not be blank."))
            .expectBody(matchesJsonSchemaInClasspath
                    ("schemas.updateUser/wrong_update_user_schema.json"))
            .build();

    public static ResponseSpecification patchSuccessfulUpdateUserRequestSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody("id", notNullValue())
            .expectBody("username", notNullValue())
            .expectBody("firstName", notNullValue())
            .expectBody("email", notNullValue())
            .expectBody("remoteAddr", notNullValue())
            .expectBody(matchesJsonSchemaInClasspath
                    ("schemas.updateUser/patch_successfull_update_user_schema.json"))
            .build();

    public static ResponseSpecification patchInvalidEmailRequestSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody("email[0]", equalTo("Enter a valid email address."))
            .expectBody(matchesJsonSchemaInClasspath
                    ("schemas.updateUser/invalid_email_schema.json"))
            .build();



}
