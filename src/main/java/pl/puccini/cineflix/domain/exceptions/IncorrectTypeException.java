package pl.puccini.cineflix.domain.exceptions;

public class IncorrectTypeException extends RuntimeException {
    public IncorrectTypeException(String message) {
        super(message);
    }
}
