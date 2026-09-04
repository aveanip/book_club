package models.updateUser;

public record UpdateBodyModel(String username,
                              String firstName,
                              String lastName,
                              String email){}