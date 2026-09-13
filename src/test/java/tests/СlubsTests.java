package tests;

import com.github.javafaker.Faker;
import models.clubs.*;
import models.login.LoginBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class СlubsTests extends TestBase {
    Faker faker = new Faker(new Locale("ru"));

    private String accessToken;
//    private Integer createdClubId;

    @BeforeEach
    public void auth() {
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        accessToken = api.auth.login(loginData).access();
    }


    @Test
    @DisplayName("Каждый клуб в списке содержит все обязательные заполненные поля")
    public void getClubsEachClubHasRequiredFieldsTest() {
        step("1. Получение списка всех клубов", () -> {
            ClubsResponseModel response = api.clubs.getClubs();
            step("2. Проверка заполнения обязательных полей у каждого клуба в списке", () -> {
                for (ClubsModel club : response.results()) {
                    assertThat(club.id()).isNotNull().isPositive();
                    assertThat(club.bookTitle()).isNotNull();
                    assertThat(club.bookAuthors()).isNotNull();
                    assertThat(club.publicationYear()).isNotNull();
                    assertThat(club.description()).isNotNull();
                    assertThat(club.telegramChatLink()).isNotNull();
                    assertThat(club.owner()).isNotNull().isPositive();
                    assertThat(club.members()).isNotNull();
                    assertThat(club.reviews()).isNotNull();
                    assertThat(club.created()).isNotNull();
                }
            });
        });
    }

    @Test
    @DisplayName("Создание нового клуба и проверка всех полей")

    public void successfulCreatingClubTest() {
        ClubBodyModel clubBody = new ClubBodyModel(
                faker.book().title(),
                faker.book().author(),
                faker.number().numberBetween(1900, 2023),
                faker.lorem().sentence(5),
                "https://t.me/test_chat_" + faker.number().randomNumber()
        );

        step("Отправка POST-запроса на создание клуба", () -> {
            ClubsModel createClub = api.clubs.createClub(accessToken, clubBody);

            step("Проверка, что созданный клуб содержит корректные данные", () -> {
                assertThat(createClub.id()).isPositive();
                assertThat(createClub.bookTitle()).isEqualTo(clubBody.bookTitle());
                assertThat(createClub.bookAuthors()).isEqualTo(clubBody.bookAuthors());
                assertThat(createClub.publicationYear()).isEqualTo(clubBody.publicationYear());
                assertThat(createClub.description()).isEqualTo(clubBody.description());
                assertThat(createClub.telegramChatLink()).isEqualTo(clubBody.telegramChatLink());
                assertThat(createClub.owner()).isPositive();
                assertThat(createClub.members()).isNotNull();
            });
        });
    }

    @Test
    @DisplayName("Создание клуба с существующим названием возвращает 400")
    public void createClubWithExistingTitleShouldReturn400Test() {

        String uniqueTitle = "Книга" + faker.number().randomNumber(6, true);

        ClubBodyModel clubData = new ClubBodyModel(
                uniqueTitle,
                faker.book().author(),
                faker.number().numberBetween(1900, 2023),
                faker.lorem().sentence(5),
                "https://t.me/test_chat_" + faker.number().randomNumber()
        );
        step("1. Успешное создание первого клуба с уникальным названием", () -> {
            ClubsModel createClub = api.clubs.createClub(accessToken, clubData);

            assertThat(createClub.id()).isPositive();
        });
        step("2. Попытка создания дубликата и проверка получения ошибки 400", () -> {
            CreateClubWithExistingTitleModel createClubWithExistingTitleModel = api.clubs.createDuplicate(accessToken, clubData);

            assertThat(createClubWithExistingTitleModel.bookTitle()).isNotNull();
            assertThat(createClubWithExistingTitleModel.bookTitle()).contains(dublicateBookTitle);
        });
    }

    @Test
    @DisplayName("Попытка создания клуба с невалидной ссылкой на телеграм чат")
    public void shouldReturnErrorWhenCreatingClubWithInvalidTelegramLinkTest() {

        String uniqueTitle = "Книга" + faker.number().randomNumber(6, true);

        ClubBodyModel clubData = new ClubBodyModel(
                uniqueTitle,
                faker.book().author(),
                faker.number().numberBetween(1900, 2026),
                faker.lorem().sentence(5),
                "https" + faker.number().randomNumber()
        );
        step("1. Отправка запроса на создание клуба с невалидной ссылкой", () -> {
            CreatingClubWithInvalidTelegramСhatURL creatingClubWithInvalidTelegramСhatURL =
                    api.clubs.clubWithInvalidTelegramСhatURL(accessToken, clubData);
            step("2. Проверка наличия ошибки валидации именно для поля telegramChatLink", () -> {
                assertThat(creatingClubWithInvalidTelegramСhatURL.telegramChatLink()).isNotNull();
                assertThat(creatingClubWithInvalidTelegramСhatURL.telegramChatLink()).contains(invalidTelegramChat);

            });
        });
    }


    @Test
    @DisplayName("Попытка создания клуба с пустыми полями")
    public void createClubEmptyFieldsTest() {
        ClubBodyModel clubData = new ClubBodyModel(
                "",
                "",
                0,
                "",
                ""
        );

        step("1. Отправка запроса на создание клуба с невалидной ссылкой", () -> {
            WrongWithEmptyClubDataModel wrongWithEmptyClubDataModel =
                    api.clubs.createClubWithEmptyFields(accessToken, clubData);
            step("2. Проверка наличия ошибки валидации именно для поля telegramChatLink", () -> {
                assertThat(wrongWithEmptyClubDataModel.bookTitle()).isNotNull();
                assertThat(wrongWithEmptyClubDataModel.bookAuthors()).isNotNull();
                assertThat(wrongWithEmptyClubDataModel.description()).isNotNull();
                assertThat(wrongWithEmptyClubDataModel.telegramChatLink()).isNotNull();
                assertThat(wrongWithEmptyClubDataModel.bookTitle()).contains(expectedErrorFieldIsEmpty);
                assertThat(wrongWithEmptyClubDataModel.bookAuthors()).contains(expectedErrorFieldIsEmpty);
                assertThat(wrongWithEmptyClubDataModel.description()).contains(expectedErrorFieldIsEmpty);
                assertThat(wrongWithEmptyClubDataModel.telegramChatLink()).contains(expectedErrorFieldIsEmpty);
            });
        });
    }

    @Test
    @DisplayName("Успешное обновление всех полей клуба по его ID")
    public void updateClubFieldsWithPutRequestTest() {
        ClubBodyModel clubBody = new ClubBodyModel(
                "Книга " + faker.book().title(),
                faker.book().author(),
                faker.number().numberBetween(1900, 2023),
                faker.lorem().sentence(5),
                "https://t.me/test_chat_" + faker.number().randomNumber()
        );

        ClubsModel createClub = api.clubs.createClub(accessToken, clubBody);
        Integer createdClubId = createClub.id();

        assertThat(createClub.id()).isPositive();
        assertThat(createClub.bookTitle()).isEqualTo(clubBody.bookTitle());
        assertThat(createClub.bookAuthors()).isEqualTo(clubBody.bookAuthors());
        assertThat(createClub.publicationYear()).isEqualTo(clubBody.publicationYear());
        assertThat(createClub.description()).isEqualTo(clubBody.description());
        assertThat(createClub.telegramChatLink()).isEqualTo(clubBody.telegramChatLink());
        assertThat(createClub.owner()).isPositive();
        assertThat(createClub.members()).isNotNull();


        ClubBodyModel updateClubBody = new ClubBodyModel(
                "Обновленное название: " + faker.book().title(),
                "Обновленный автор: " + faker.book().author(),
                faker.number().numberBetween(2024, 2030),
                "Обновленное описание: " + faker.lorem().sentence(3),
                "https://t.me/updated_chat_" + faker.number().randomNumber());

        ClubsModel updatedClub = api.clubs.updatingAllTheClubFieldsPUT(accessToken, createdClubId, updateClubBody);
        assertThat(updatedClub.id()).isPositive();
        assertThat(updatedClub.bookTitle()).isEqualTo(updatedClub.bookTitle());
        assertThat(updatedClub.bookAuthors()).isEqualTo(updatedClub.bookAuthors());
        assertThat(updatedClub.publicationYear()).isEqualTo(updatedClub.publicationYear());
        assertThat(updatedClub.description()).isEqualTo(updatedClub.description());
        assertThat(updatedClub.telegramChatLink()).isEqualTo(updatedClub.telegramChatLink());
        assertThat(updatedClub.owner()).isPositive();
        assertThat(updatedClub.members()).isNotNull();
    }


    //Создание негативных тестов на Put (3-4 теста)
    //Создать тесты на обновления с Patch + негативные тесты(1 позитивные + 3-4 негативных)
    //Создать тесты на позитивные и негативные проверки на удаление книжного клуба (1 позитив + 2-3 негатив)

}