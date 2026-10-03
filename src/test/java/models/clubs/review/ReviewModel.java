package models.clubs.review;

import models.clubs.ClubsUsersModel;

// модель при 200 статус коде (создание)
public record ReviewModel(Integer id,
                          Integer club,
                          ClubsUsersModel user,
                          String review,
                          Integer assessment,
                          Integer readPages,
                          String created,
                          String modified
) {}
