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
















}
