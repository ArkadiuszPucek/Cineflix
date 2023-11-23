package pl.puccini.cineflix.domain.exceptions;

public class PasswordFormatException extends Throwable {
    public PasswordFormatException(String message) {
        super(message);
    }
}
