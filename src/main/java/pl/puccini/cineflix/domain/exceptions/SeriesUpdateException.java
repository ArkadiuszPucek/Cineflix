package pl.puccini.cineflix.domain.exceptions;

public class SeriesUpdateException extends RuntimeException {
    public SeriesUpdateException(String message) {
        super(message);
    }
}
