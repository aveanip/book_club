package tests.UI;

import com.github.javafaker.Faker;
import models.clubs.ClubBodyModel;
import models.clubs.ClubsModel;
import models.login.LoginBodyModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ClubPage;
import tests.TestBase;
import java.util.Locale;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;


public class ClubTests extends TestBase {


    Faker faker = new Faker(new Locale("en"));
    String bookTitle = "GURU-QA " + faker.book().title();
    String bookAuthors = faker.book().author();
    int publicationYear = faker.number().numberBetween(1900, 2026);
    String description = faker.lorem().sentence(5);
    String telegramChatLink = "https://t.me/test_chat_" + faker.number().randomNumber();
    String password = "12345";
    String uniqueUsername = faker.name().username();

    ClubPage clubPage = new ClubPage();

    @Test
    @DisplayName("[API] Владелец клуба не может покинуть клуб")
    public void cantLeaveClubAsAdminTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(uniqueUsername, password);
        RegistrationResponseModel registrationResponse = api.user.successfulUserRegistration(registrationData);
        LoginBodyModel loginBody = new LoginBodyModel(uniqueUsername, password);
        LoginResponseModel loginResponse = api.auth.login(loginBody);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        String localStorageAuthBody = """
                               {"user": {
                                "id": %d,
                                "username":"%s",
                                "firstName": "%s",
                                "LastName": "%s",
                                "email": "%s",
                                "remoteAddr": "%s"
                                },
                                 "accessToken": "%s",
                                "refreshToken": "%s",
                                "isAuthenticated": true
                } 
                """.formatted(
                registrationResponse.id(),
                registrationResponse.username(),
                registrationResponse.firstName(),
                registrationResponse.lastName(),
                registrationResponse.email(),
                registrationResponse.remoteAddr(),
                accessToken,
                refreshToken
        );

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubsModel clubsModel = api.clubs.createClub(accessToken, createClubBody);
        int cludId = clubsModel.id();

        clubPage
                .openPage()
                .putAuthIntoLocalStorage(localStorageAuthBody)
                .openClubInfoPage(cludId)
                .checkClubInfo()
                .clickLeaveBtn()
                .confirmLeaveClub()
                .checkTextClubError("Не удалось покинуть клуб");

//
//        open("https://book-club.qa.guru");
//        localStorage().setItem("book_club_auth", localStorageAuthBody);
//        open("https://book-club.qa.guru/clubs/" + cludId);
//
//        $(".club-content").shouldBe(visible);
//        $(".leave-btn").click();
//        confirm();
//        $(".error").$(byText("Не удалось покинуть клуб"));
    }


}
