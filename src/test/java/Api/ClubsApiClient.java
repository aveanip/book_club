package Api;

import io.qameta.allure.Step;
import models.clubs.*;

import static io.restassured.RestAssured.given;
import static specs.clubs.ClubsSpec.*;

public class ClubsApiClient {


    @Step("Получение списка клубов GET /clubs/")
    public ClubsResponseModel getClubs() {
        return given(clubsRequestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubsListResponseSpec)
                .extract()
                .as(ClubsResponseModel.class);
    }

    @Step("Проверка пагинации ")
    public ClubsResponseModel getClubsListWithPagination(String accessToken, int page, int pageSize) {
        return given(clubsRequestSpec)
                .auth().oauth2(accessToken)
                .queryParam("page", page)
                .queryParam("page_size", pageSize)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubsListResponseSpec)
                .extract()
                .as(ClubsResponseModel.class);
    }

    @Step("Отправка POST-запроса на создание книжного клуба с валидными данными")
    public ClubsModel createClub(String accessToken, ClubBodyModel clubBody){
        return given(clubsRequestSpec)
                .auth().oauth2(accessToken)
                .body(clubBody)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClub)
                .extract()
                .as(ClubsModel.class);
    }

    @Step("Отправка POST-запроса на создание дубликата клуба и ожидание ошибки валидации названия")
    public CreateClubWithExistingTitleModel createDuplicate(String accessToken, ClubBodyModel clubBody){
        return given(clubsRequestSpec)
                .auth().oauth2(accessToken)
                .body(clubBody)
                .when()
                .post("/clubs/")
                .then()
                .spec(createClubWithDuplicateTitle)
                .extract()
                .as(CreateClubWithExistingTitleModel.class);
    }


    @Step("Отправка POST-запроса на создание клуба с некорректным URL чата и ожидание ошибки валидации (400)")
    public CreatingClubWithInvalidTelegramСhatURL clubWithInvalidTelegramСhatURL(String accessToken, ClubBodyModel clubBody){
        return given(clubsRequestSpec)
                .auth().oauth2(accessToken)
                .body(clubBody)
                .when()
                .post("/clubs/")
                .then()
                .spec(validationErrorWhenTheTelegramChatURLIsInvalid)
                .extract()
                .as(CreatingClubWithInvalidTelegramСhatURL.class);
    }
    @Step("Отправка POST-запроса на создание клуба с пустыми значениями во всех полях")
    public WrongWithEmptyClubDataModel createClubWithEmptyFields (String accessToken, ClubBodyModel clubBody){
        return given(clubsRequestSpec)
                .auth().oauth2(accessToken)
                .body(clubBody)
                .when()
                .post("/clubs/")
                .then()
                .spec(createClubForAllTheEmptyFields)
                .extract()
                .as(WrongWithEmptyClubDataModel.class);
    }

    @Step("Обновление информации всех полей книжного клуба через ручку PUT /clubs/{id}/")
    public ClubsModel updatingAllTheClubFieldsPUT (String accessToken, Integer id, ClubBodyModel clubBody){
        return given(clubsRequestSpec)
                .body(clubBody)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", id)
                .put("/clubs/{id}/")
                .then()
                .spec(updatingAllFieldsPUT)
                .extract()
                .as(ClubsModel.class);
    }

    @Step("Обновление данных клуба через PUT с установкой пустых значений для назавния и ссылки Telegram")
    public WrongWithEmptyClubDataModel emptyFieldsBookTitleAndTelegramChatLink (String accessToken, Integer id, ClubBodyModel clubBody){
        return given(clubsRequestSpec)
                .body(clubBody)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", id)
                .put("/clubs/{id}/")
                .then()
                .spec( createClubEmptyFields)
                .extract()
                .as(WrongWithEmptyClubDataModel.class);
    }

    @Step("PUT запрос с null во всех полях клуба должен возвращать ошибку валидации")
    public WrongWithEmptyClubDataModel updateClubWithAllNullFieldsReturnsValidationError (String accessToken, Integer id, ClubBodyModel clubBody){
        return given(clubsRequestSpec)
                .body(clubBody)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", id)
                .put("/clubs/{id}/")
                .then()
                .spec(updatingAllFieldsNullPUT)
                .extract()
                .as(WrongWithEmptyClubDataModel.class);
    }

    @Step("PATCH: частичное обновление bookTitle, bookAuthors и publicationYear")
    public ClubsModel updateClubBookDetails (String accessToken, Integer id, ClubBodyPatchModel clubBodyPatch){
        return given(clubsRequestSpec)
                .body(clubBodyPatch)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", id)
                .patch("/clubs/{id}/")
                .then()
                .spec(partialModificationOfFieldsPATCH)
                .extract()
                .as(ClubsModel.class);
    }
    @Step("Удаление клуба по ID")
    public void deleteClub (String accessToken, Integer id){
        given(clubsRequestSpec)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", id)
                .delete("/clubs/{id}/")
                .then()
                .spec(deletedClubResponseSpec);
    }

    @Step("Попытка получения данных несуществующего клуба (ожидается 404)")
    public ErrorClubModel сheckingTheBookClubByID (String accessToken, Integer id) {
        return given(clubsRequestSpec)
                .auth().oauth2(accessToken)
                .when()
                .pathParam("id", id)
                .get("/clubs/{id}/")
                .then()
                .spec(сheckingTheBookClubByID)
                .extract()
                .as(ErrorClubModel.class);

    }
















}
