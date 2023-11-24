package pl.puccini.cineflix.domain.exceptions;

public class UserNotLoginAuthentication extends  RuntimeException{
    public UserNotLoginAuthentication(String message) {
        super(message);
    }
}
