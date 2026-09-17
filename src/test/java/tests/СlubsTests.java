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
    private String accessToken;

    @BeforeEach
    public void auth() {
        Faker faker = new Faker(new Locale("ru"));
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        accessToken = api.auth.login(loginData).access();
    }

    private ClubBodyModel generateRandomClub() {
        return new ClubBodyModel(
                "GURU-QA " + faker.book().title(),
                faker.book().author(),
                faker.number().numberBetween(1900, 2023),
                faker.lorem().sentence(5),
                "https://t.me/test_chat_" + faker.number().randomNumber());
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
    @DisplayName("Проверка пагинации списка клубов")
    public void checkPaginationTest() {
        ClubsResponseModel page = step("Получение первой страницы списка клубов (page=1, page_size=20) ", () ->
                api.clubs.getClubsListWithPagination(accessToken, 1, 20)
        );

        step("Проверка общего количества клубов в ответе", () ->
                assertThat(page.count())
                        .as("Общее количество клубов должно быть больше 0")
                        .isGreaterThan(0)
        );
        step("Проверка количества элементов на первой странице", () ->
                assertThat(page.results().size())
                        .as("Количество элементов на странице должно быть равно pageSize ")
                        .isLessThanOrEqualTo(20));
        step("Проверка наличия ссылки на следующую страницу", () -> {
            if (page.count() > 20) {
                assertThat(page.next())
                        .as("Если клубов больше page_size, next должен содержать ссылку на следующую страницу")
                        .isNotNull()
                        .contains("page=2");
            }
        });
    }

    @Test
    @DisplayName("Создание нового клуба и проверка всех полей")

    public void successfulCreatingClubTest() {
        ClubBodyModel clubBody = generateRandomClub();

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
        ClubBodyModel clubData = generateRandomClub();

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
        ClubBodyModel clubData = new ClubBodyModel(
                "GURU-QA " + faker.book().title(),
                faker.book().author(),
                faker.number().numberBetween(1900, 2023),
                faker.lorem().sentence(5),
                "https:/"
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
        ClubBodyModel clubBody = generateRandomClub();


        ClubsModel createClub = step("1. Отправка запроса на создание клуба и проверка уникального ID", () ->
                api.clubs.createClub(accessToken, clubBody)
        );
        Integer createdClubId = createClub.id();
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

        step("Обновление данных книжного клуба", () -> {
            ClubBodyModel updateClubBody = new ClubBodyModel(
                    "Обновленное название: " + faker.book().title(),
                    "Обновленный автор: " + faker.book().author(),
                    faker.number().numberBetween(2024, 2030),
                    "Обновленное описание: " + faker.lorem().sentence(3),
                    "https://t.me/updated_chat_" + faker.number().randomNumber());
            step("Проверка, что ранее созданный клуб обновил данные во всех полях", () -> {
                ClubsModel updatedClub = api.clubs.updatingAllTheClubFieldsPUT(accessToken, createdClubId, updateClubBody);
                assertThat(updatedClub.id()).isPositive();
                assertThat(updatedClub.bookTitle()).isEqualTo(updatedClub.bookTitle());
                assertThat(updatedClub.bookAuthors()).isEqualTo(updatedClub.bookAuthors());
                assertThat(updatedClub.publicationYear()).isEqualTo(updatedClub.publicationYear());
                assertThat(updatedClub.description()).isEqualTo(updatedClub.description());
                assertThat(updatedClub.telegramChatLink()).isEqualTo(updatedClub.telegramChatLink());
                assertThat(updatedClub.owner()).isPositive();
                assertThat(updatedClub.members()).isNotNull();
            });
        });
    }

    @Test
    @DisplayName("Обновление данных клуба через PUT с установкой пустых значений для названия и ссылки Telegram")
    public void checkValidationOnUpdateClubWithEmptyTitleAndTelegramTest() {
        ClubBodyModel clubBody = generateRandomClub();

        ClubsModel createClub =
                api.clubs.createClub(accessToken, clubBody);

        Integer createdClubId = createClub.id();
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

        step("Обновление данных книжного клуба", () -> {
            ClubBodyModel updateClubBody = new ClubBodyModel(
                    "",
                    "Обновленный автор: " + faker.book().author(),
                    faker.number().numberBetween(2024, 2030),
                    "Обновленное описание: " + faker.lorem().sentence(3),
                    ""
            );
            step("Проверка, сообщения валидации для полей bookTitle и telegramChatLink", () -> {
                WrongWithEmptyClubDataModel wrongWithEmptyClubData = api.clubs.emptyFieldsBookTitleAndTelegramChatLink(accessToken, createdClubId, updateClubBody);
                assertThat(wrongWithEmptyClubData.bookTitle()).isNotNull()
                        .as("Сервер должен вернуть ошибку для пустого поля bookTitle")
                        .contains(expectedErrorFieldIsEmpty);
                assertThat(wrongWithEmptyClubData.telegramChatLink()).isNotNull()
                        .as("Сервер должен вернуть ошибку для пустого поля telegramChatLink")
                        .contains(expectedErrorFieldIsEmpty);
            });
        });
    }

    @Test
    @DisplayName("Обновление всех полей клуба значениями null возвращает ошибку")
    public void shouldFailToUpdateClubWhenAllFieldsAreNullTest() {
        ClubBodyModel clubBody = generateRandomClub();

        ClubsModel createClub =
                api.clubs.createClub(accessToken, clubBody);

        Integer createdClubId = createClub.id();
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

        step("Обновление данных книжного клуба", () -> {
            ClubBodyModel updateClubBody = new ClubBodyModel(
                    null,
                    null,
                    null,
                    null,
                    null
            );
            step("Проверка, сообщения валидации для полей bookTitle и telegramChatLink", () -> {
                WrongWithEmptyClubDataModel updateClubWithAllNullFieldsError = api.clubs.updateClubWithAllNullFieldsReturnsValidationError(accessToken, createdClubId, updateClubBody);
                assertThat(updateClubWithAllNullFieldsError.bookTitle()).isNotNull()
                        .as("Сервер должен вернуть ошибку для пустого поля bookTitle")
                        .contains(detailValidError);
                assertThat(updateClubWithAllNullFieldsError.bookAuthors()).isNotNull()
                        .as("Сервер должен вернуть ошибку для пустого поля bookAuthors")
                        .contains(detailValidError);
                assertThat(updateClubWithAllNullFieldsError.description()).isNotNull()
                        .as("Сервер должен вернуть ошибку для пустого поля description")
                        .contains(detailValidError);
                assertThat(updateClubWithAllNullFieldsError.telegramChatLink()).isNotNull()
                        .as("Сервер должен вернуть ошибку для пустого поля telegramChatLink")
                        .contains(detailValidError);
            });
        });
    }

    @Test
    @DisplayName("Успешное создание книжного клуба и обновление информации о книге")
    public void shouldCreateAndUpdateBookClubTest() {
        ClubBodyModel clubBody = generateRandomClub();

        ClubsModel createClub =
                api.clubs.createClub(accessToken, clubBody);

        Integer createdClubId = createClub.id();

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

        ClubBodyPatchModel updateClubBody = step("Обновление данных книжного клуба (только title, authors, year)", () ->
                new ClubBodyPatchModel(
                        "Книга " + faker.book().title(),
                        faker.book().author(),
                        faker.number().numberBetween(1900, 2023))
        );

        ClubsModel updatedClub = api.clubs.updateClubBookDetails(accessToken, createdClubId, updateClubBody);

        step("Проверка успешного применения обновленных данных книги", () -> {
            assertThat(updatedClub.bookTitle()).isEqualTo(updateClubBody.bookTitle());
            assertThat(updatedClub.bookAuthors()).isEqualTo(updateClubBody.bookAuthors());
            assertThat(updatedClub.publicationYear()).isEqualTo(updateClubBody.publicationYear());
        });
    }

    @Test
    @DisplayName("Delete: успешное удаление клуба (204) и последующий 404 при поиске через ID")
    public void deleteBookClubTest() {
        ClubBodyModel clubBody = generateRandomClub();

        ClubsModel createClub = step("Создание нового книжного клуба", () ->
                api.clubs.createClub(accessToken, clubBody)
        );
        Integer createdClubId = createClub.id();
        step("Удаление только что созданного книжного клуба", () ->
                api.clubs.deleteClub(accessToken, createdClubId)
        );

        ErrorClubModel ckeckBookClubId = step("Проверка только что удаленного книжного клуба по его Id", () ->
                api.clubs.сheckingTheBookClubByID(accessToken, createdClubId)
        );

        step("Проверить сообщение об ошибке 404 Not Found", () ->
                assertThat(ckeckBookClubId.detail()).isNotNull()
                        .as("Сервер должен вернуть ошибку 404 для удаленного клуба")
                        .contains(notFoundError)
        );


    }
}
