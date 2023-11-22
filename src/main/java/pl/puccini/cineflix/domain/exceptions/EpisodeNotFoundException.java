package pl.puccini.cineflix.domain.exceptions;

public class EpisodeNotFoundException extends RuntimeException {
    public EpisodeNotFoundException(String s) {
        super(s);
    }
}
