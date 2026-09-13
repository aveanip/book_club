package specs.clubs;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;


public class ClubsSpec {

    public static RequestSpecification clubsRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulClubsListResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody("count", notNullValue())
            .expectBody("count", greaterThanOrEqualTo(0))
            .expectBody("results", notNullValue())
            .build();


    public static ResponseSpecification successfulCreateClub = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas.clubs/Club_response_schema.json"))
            .expectBody("id", notNullValue())
            .build();

    public static ResponseSpecification createClubWithDuplicateTitle = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas.clubs/Club_duplicate_title_schema.json"))
            .build();

    public static ResponseSpecification validationErrorWhenTheTelegramChatURLIsInvalid = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas.clubs/Create_club_invalid_telegram_chat_url_schema.json"))
            .build();

    public static ResponseSpecification createClubForAllTheEmptyFields = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas.clubs/Create_club_emply_fields_error_text_schems.json"))
            .build();

    public static ResponseSpecification updatingAllFieldsPUT = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas.clubs/Updating_all_fields_of_the_book_club_PUT_schema.json"))
            .build();
}


