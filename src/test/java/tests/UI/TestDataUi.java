package tests.UI;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import java.util.Locale;

public class TestDataUi {


    Faker faker = new Faker(new Locale("en"));
    String bookTitle = "GURU-QA " + faker.book().title();
    String bookAuthors = faker.book().author();
    int publicationYear = faker.number().numberBetween(1900, 2026);
    String description = faker.lorem().sentence(5);
    String telegramChatLink = "https://t.me/test_chat_" + faker.number().randomNumber();
    String password = "12345";
    String uniqueUsername = faker.name().username();
}