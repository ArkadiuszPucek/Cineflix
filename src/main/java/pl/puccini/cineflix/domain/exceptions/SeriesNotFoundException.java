package pl.puccini.cineflix.domain.exceptions;

public class SeriesNotFoundException extends RuntimeException{
    public SeriesNotFoundException(String message) {
        super(message);
    }
}
