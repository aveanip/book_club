package models.clubs;

import java.util.List;

public record ClubsModel(Integer id,
                        String bookTitle,
                        String bookAuthors,
                        Integer publicationYear,
                        String description,
                        String telegramChatLink,
                        Integer owner,
                        List<Integer> members,
                        List<ClubsReviewModel> reviews,
                        String created,
                        String modified) {}
