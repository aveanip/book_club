package models.login;

import java.util.List;

public record EmptyCredentialsLoginResponseModel (List<String> username,
                                                 List<String> password) {}
