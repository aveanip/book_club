package models.clubs;

public record ClubsReviewModel (Integer id,
                               Integer club,
                               ClubsUsersModel user,
                               String review,
                               Integer assessment,
                               Integer readPages,
                               String created,
                               String modified){}
