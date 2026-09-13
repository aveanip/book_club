package models.clubs;

import java.util.List;

public record WrongWithEmptyClubDataModel(List<String> bookTitle,
                                          List<String> bookAuthors,
                                          List<String> description,
                                          List<String> telegramChatLink){}
