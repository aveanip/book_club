package specs.review;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.clubs.ClubsUsersModel;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class ReviewSpecs {
    public static RequestSpecification reviewRequestSpec = baseRequestSpec;

//    public static final ResponseSpecification reviewsListResponse200Spec = new ResponseSpecBuilder()
//            .log(ALL)
//            .expectStatusCode(200)
//            .build();

    public static ResponseSpecification successfulСreationSpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas.review/successful_creation_review_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("club", greaterThanOrEqualTo(0))
            .expectBody("user", notNullValue())
            .expectBody("review", notNullValue())
            .expectBody("assessment", notNullValue())
            .expectBody("readPages", notNullValue())
            .expectBody("created", notNullValue())
            .build();

    public static ResponseSpecification creationEmptySpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody("review", notNullValue())
            .build();

    public static ResponseSpecification successfulSearchForReviewByIDSpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas.review/search_by_id_review_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("club", greaterThanOrEqualTo(0))
            .expectBody("user", notNullValue())
            .expectBody("review", notNullValue())
            .expectBody("assessment", notNullValue())
            .expectBody("readPages", notNullValue())
            .expectBody("created", notNullValue())
            .build();

    public static ResponseSpecification unsuccessfulSearchForReviewByIDSpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas.review/wrong_by_id_review_schema.json"))
            .expectBody("detail", notNullValue())
            .build();

    public static ResponseSpecification successfulReviewChangeAllFieldsSpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas.review/successful_review_change_all_fields_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("club", greaterThanOrEqualTo(0))
            .expectBody("user", notNullValue())
            .expectBody("review", notNullValue())
            .expectBody("assessment", notNullValue())
            .expectBody("readPages", notNullValue())
            .expectBody("created", notNullValue())
            .expectBody("modified", notNullValue())
            .build();

    public static ResponseSpecification emptyReviewFieldSpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody("review", notNullValue())
            .build();

    public static ResponseSpecification deleteReviewSpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();

    public static ResponseSpecification deleteWithoutPermissionSpecs =  new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(403)
            .build();















}
