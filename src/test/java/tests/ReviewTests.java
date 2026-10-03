package tests;

import com.github.javafaker.Faker;
import models.clubs.ClubBodyModel;
import models.clubs.ClubsModel;
import models.clubs.review.CreateReviewRequestModel;
import models.clubs.review.ErrorWhenCreatingAnEmptyReviewModel;
import models.clubs.review.ReviewModel;
import models.clubs.review.WrongReviewModel;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Locale;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class ReviewTests extends TestBase {
    private String accessToken;
    private String username;
    private String password;
    private Integer createdClubId;
    Faker faker = new Faker(new Locale("en"));
    String uniqueTitle = "GURU-QA " + faker.book().title() + " " + faker.number().randomNumber();
    private ClubBodyModel generateRandomClub() {
        return new ClubBodyModel(
                uniqueTitle,
                faker.book().author(),
                faker.number().numberBetween(1900, 2023),
                faker.lorem().sentence(5),
                "https://t.me/test_chat_" + faker.number().randomNumber());
    }

    @BeforeEach
    public void auth() {
        //Генерируем данные пользователя
        username = faker.name().firstName().toLowerCase() + faker.number().randomDigit();
        password = "TestPass123" + faker.number().randomDigit();

        //РЕГИСТРИРУЕМ пользователя перед логином
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.user.successfulUserRegistration(registrationData); // Сначала регистрируем

        //Теперь логинимся
        LoginBodyModel loginData = new LoginBodyModel(username, password);
        accessToken = api.auth.login(loginData).access();

        //Создаем клуб
        ClubsModel createdClub = api.clubs.createClub(accessToken, generateRandomClub());
        createdClubId = createdClub.id();
    }


    @Test
    @DisplayName("Попытка создать пустое ревью")

    public void SuccessTest() {
        CreateReviewRequestModel data = new CreateReviewRequestModel(
                createdClubId,
                "",
                faker.number().numberBetween(1, 6),
                faker.number().numberBetween(1, 500)
        );

        ErrorWhenCreatingAnEmptyReviewModel errorWhenCreatingAnEmptyReview =
                api.review.createEmptyReview(accessToken, data);
        step("Проверка ошибки пустого review", () -> {
            assertThat(errorWhenCreatingAnEmptyReview.review()).contains("This field may not be blank.");

        });
    }


    @Test
    @DisplayName("Успешное создание ревью с валидными данными")

    public void createReviewSuccessTest() {
        CreateReviewRequestModel data = new CreateReviewRequestModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 6),
                faker.number().numberBetween(1, 500)
        );


        ReviewModel reviewModel =
                step("[API] Создание отзыва", () -> api.review.createReview(accessToken, data));
        step("Проверка полей отзыва", () -> {
            assertThat(reviewModel.id()).isPositive();
            assertThat(reviewModel.club()).isEqualTo(createdClubId);
            assertThat(reviewModel.user()).isNotNull();
            assertThat(reviewModel.user().username()).isEqualTo(username);
            assertThat(reviewModel.review()).isEqualTo(data.review());
            assertThat(reviewModel.assessment()).isEqualTo(data.assessment());
            assertThat(reviewModel.readPages()).isEqualTo(data.readPages());
            assertThat(reviewModel.created()).isNotNull();
            assertThat(reviewModel.modified()).isNull();
        });
    }

    @Test
    @DisplayName("Успешный поиск ревью по id")
    public void searchForReviewsByIDTest() {
        CreateReviewRequestModel data = new CreateReviewRequestModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 6),
                faker.number().numberBetween(1, 500)
        );

        ReviewModel reviewModel =
                step("[API] Создание отзыва", () -> api.review.createReview(accessToken, data));

        Integer createReview = reviewModel.id();
        step("[API] Проверка полей созданного отзыва", () -> {
            assertThat(createReview).isPositive();
            assertThat(reviewModel.club()).isEqualTo(createdClubId);
            assertThat(reviewModel.user()).isNotNull();
            assertThat(reviewModel.user().username()).isEqualTo(username);
            assertThat(reviewModel.review()).isEqualTo(data.review());
            assertThat(reviewModel.assessment()).isEqualTo(data.assessment());
            assertThat(reviewModel.readPages()).isEqualTo(data.readPages());
            assertThat(reviewModel.created()).isNotNull();
            assertThat(reviewModel.modified()).isNull();
        });

        ReviewModel foundReview = step("[API] Поиск отзыва по ID", () ->
                api.review.searchForReviewsByID(createReview));

        step("Проверка полей отзыва", () -> {
            assertThat(foundReview.id()).isPositive();
            assertThat(foundReview.club()).isEqualTo(createdClubId);
            assertThat(foundReview.user()).isNotNull();
            assertThat(foundReview.user().username()).isEqualTo(username);
            assertThat(foundReview.review()).isEqualTo(data.review());
            assertThat(foundReview.assessment()).isEqualTo(data.assessment());
            assertThat(foundReview.readPages()).isEqualTo(data.readPages());
            assertThat(foundReview.created()).isNotNull();
            assertThat(foundReview.modified()).isNull();
        });
    }

    @Test
    @DisplayName("Поиск отзыва по несуществующему ID возвращает ошибку 404")

    public void invalidSearchForReviewsByIDTest() {

        WrongReviewModel wrongReview = step("[API] Поиск отзыва по невалидному ID", () ->
                api.review.unsuccessfulSearchByReviewID(invalidReviewId));

        step("[API] Проверка текста ошибки ", () -> {
            assertThat(wrongReview.detail()).contains("No BookReview matches the given query.");
        });
    }

    @Test
    @DisplayName("Попытка обновить отзыв с пустым текстом возвращает ошибку 400")
    public void updateReviewWithEmptyTextReturnsErrorTest() {
        CreateReviewRequestModel data = new CreateReviewRequestModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 6),
                faker.number().numberBetween(1, 10000)
        );

        ReviewModel reviewModel = step("[API] Создание отзыва для последующего обновления", () ->
                api.review.createReview(accessToken, data));

        Integer reviewId = reviewModel.id();

        CreateReviewRequestModel updateData = new CreateReviewRequestModel(
                createdClubId,
                "",
                5,
                500
        );

        ErrorWhenCreatingAnEmptyReviewModel emplyReview =
                step("[API] Попытка обновления с пустым текстом review", () ->
                        api.review.emplyFieldReview(accessToken, reviewId, updateData));

        step("[API] Проверка обновленных данных", () -> {
            assertThat(emplyReview.review()).isNotEmpty();
            assertThat(emplyReview.review().get(0)).contains("This field may not be blank.");
        });
    }

    @Test
    @DisplayName("Удачная попытка удаление review")
    public void deleteReviewTest() {
        CreateReviewRequestModel data = new CreateReviewRequestModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 6),
                faker.number().numberBetween(1, 10000)
        );
        ReviewModel reviewModel = step("[API] Создание отзыва", () ->
                api.review.createReview(accessToken, data));
        Integer reviewId = reviewModel.id();

        api.review.deleteReview(accessToken, reviewId);

        WrongReviewModel wrongReview = step("[API] Поиск отзыва по несуществующему ID", () ->
                api.review.unsuccessfulSearchByReviewID(invalidReviewId));

        step("[API] Проверка текста ошибки ", () -> {
            assertThat(wrongReview.detail()).contains("No BookReview matches the given query.");
        });
    }

    @Test
    @DisplayName("Попытка удаление review сторонним пользователем")
    public void deleteReviewByThirdPartyUserTest() {
        CreateReviewRequestModel data = new CreateReviewRequestModel(
                createdClubId,
                faker.lorem().sentence(),
                faker.number().numberBetween(1, 6),
                faker.number().numberBetween(1, 10000)
        );
        ReviewModel reviewModel = step("[API] Создание отзыва", () ->
                api.review.createReview(accessToken, data));
        Integer reviewId = reviewModel.id();

        LoginBodyModel loginData = new LoginBodyModel(user, passwordUser);
        accessToken = api.auth.login(loginData).access();

       WrongReviewModel wrongReview = api.review.deleteReviewWithoutPermission(accessToken,reviewId);
        step("[API] Проверка текста ошибки ", () -> {
            assertThat(wrongReview.detail()).contains("You do not have permission to perform this action.");
        });
    }
}
