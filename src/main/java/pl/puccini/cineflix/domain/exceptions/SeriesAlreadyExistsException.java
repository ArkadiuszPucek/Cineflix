package pl.puccini.cineflix.domain.exceptions;

public class SeriesAlreadyExistsException extends RuntimeException{
    public SeriesAlreadyExistsException(String message) {
        super(message);
    }

}
