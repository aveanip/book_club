package tests;

import com.github.javafaker.Faker;

public class TestData {

    static Faker faker = new Faker();

    public static final String username = "user123";
    public static final String password = "User12345";
    public static final String wrongPassword = "User1234";
    public static final String wrongUsername = "User1";
    public static final String invalidEmail = "test@ya.";
    public static final String expectedDetailError = "Token is invalid";
    public static final String expectedCodeError = "token_not_valid";
    public static final String invalidRefreshToken = "token";
    public static final String newUsername = faker.name().username();
    public static final String newFirstName = faker.name().lastName();
    public static final String newLastName = faker.name().lastName();
    public static final String newEmail = faker.internet().emailAddress();
    public static final String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String expectedDataError = "Invalid username or password.";
    public static final String expectedError = "A user with that username already exists.";
    public static final String expectedErrorFieldIsEmpty = "This field may not be blank.";
    public static final String expectedErrorEnterValidEmailAddress = "Enter a valid email address.";
}
