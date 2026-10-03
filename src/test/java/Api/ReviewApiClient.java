package Api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.clubs.review.CreateReviewRequestModel;
import models.clubs.review.ErrorWhenCreatingAnEmptyReviewModel;
import models.clubs.review.ReviewModel;
import models.clubs.review.WrongReviewModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.review.ReviewSpecs.*;

public class ReviewApiClient {


    @Step("Создание ревью POST /clubs/reviews/")
    public ReviewModel createReview(String accessToken, CreateReviewRequestModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(successfulСreationSpecs)
                .extract()
                .as(ReviewModel.class);
    }

    @Step("Создание пустого ревью POST /clubs/reviews/ ошибка 400")
    public ErrorWhenCreatingAnEmptyReviewModel createEmptyReview(String accessToken, CreateReviewRequestModel body) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/reviews/")
                .then()
                .spec(creationEmptySpecs)
                .extract()
                .as(ErrorWhenCreatingAnEmptyReviewModel.class);
    }

    @Step("Поиск ревью по id GET /api/v1/clubs/reviews/{id}/")
    public ReviewModel searchForReviewsByID(int reviewId) {
        return given(baseRequestSpec)
                .pathParam("id", reviewId)
                .when()
                .get("/clubs/reviews/{id}/")
                .then()
                .spec(successfulSearchForReviewByIDSpecs)
                .extract()
                .as(ReviewModel.class);
    }

    @Step("Неудачный поиск ревью по id GET /api/v1/clubs/reviews/{id}/ ошибка 404")
    public WrongReviewModel unsuccessfulSearchByReviewID(int invalidId) {
        return given(baseRequestSpec)
                .pathParam("id", invalidId)
                .when()
                .get("/clubs/reviews/{id}/")
                .then()
                .spec(unsuccessfulSearchForReviewByIDSpecs)
                .extract()
                .as(WrongReviewModel.class);
    }

    @Step("Изменения review PUT /api/v1/clubs/reviews/{id}/")
    public ReviewModel successfulReviewChange(String accessToken, int reviewId, CreateReviewRequestModel updateData) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateData)
                .pathParam("id", reviewId)
                .when()
                .put("/clubs/reviews/{id}/")
                .then()
                .spec(successfulReviewChangeAllFieldsSpecs)
                .extract()
                .as(ReviewModel.class);
    }

    @Step("Изменения review PUT /api/v1/clubs/reviews/{id}/ ошибка при пустом поле review")
    public ErrorWhenCreatingAnEmptyReviewModel emplyFieldReview(String accessToken, int reviewId, CreateReviewRequestModel updateData) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateData)
                .pathParam("id", reviewId)
                .when()
                .put("/clubs/reviews/{id}/")
                .then()
                .spec(emptyReviewFieldSpecs)
                .extract()
                .as(ErrorWhenCreatingAnEmptyReviewModel.class);
    }


    @Step("Удаление review DELETE /api/v1/clubs/reviews/{id}/ ошибка при пустом поле review")
    public void deleteReview(String accessToken, Integer reviewId) {
        given(baseRequestSpec)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", reviewId)
                .delete("/clubs/reviews/{id}/")
                .then()
                .spec(deleteReviewSpecs);
    }

    @Step("Попытка удаление review DELETE /api/v1/clubs/reviews/{id}/ сторонним пользователем")
    public WrongReviewModel deleteReviewWithoutPermission(String accessToken, Integer reviewId) {
        return given(baseRequestSpec)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", reviewId)
                .delete("/clubs/reviews/{id}/")
                .then()
                .spec(deleteWithoutPermissionSpecs)
                .extract()
                .as(WrongReviewModel.class);
    }
}
