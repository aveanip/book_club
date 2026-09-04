package models.updateUser;

public record PutSuccessfullUpDateUserModel(Integer id,
                                            String username,
                                            String firstName,
                                            String lastName,
                                            String email,
                                            String remoteAddr) {
}
