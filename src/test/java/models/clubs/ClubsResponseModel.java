package models.clubs;

import java.util.List;

public record ClubsResponseModel(
        Integer count,
        String next,
        String previous,
        List<ClubsModel> results) {
}