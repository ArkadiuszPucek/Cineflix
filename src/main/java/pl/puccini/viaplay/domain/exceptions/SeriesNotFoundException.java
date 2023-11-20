package pl.puccini.viaplay.domain.exceptions;

public class SeriesNotFoundException extends RuntimeException{
    public SeriesNotFoundException(String message) {
        super(message);
    }
}
