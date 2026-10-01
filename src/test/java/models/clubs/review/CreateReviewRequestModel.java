package models.clubs.review;
//модель на запрос создания комментария

public record CreateReviewRequestModel( Integer club,
                                        String review,
                                        Integer assessment,
                                        Integer readPages) {}
