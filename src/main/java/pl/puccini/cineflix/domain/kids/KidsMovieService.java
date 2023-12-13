package pl.puccini.cineflix.domain.kids;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;

import java.util.List;

@Service
public class KidsMovieService {
    private final MovieFacade movieFacade;
    private static final String KIDS_GENRE = "Kids";

    public KidsMovieService(MovieFacade movieFacade) {
        this.movieFacade = movieFacade;
    }


    public List<MovieDto> getAllKidsMovies(Long userId) {
        return movieFacade.getMovieByGenre(KIDS_GENRE, userId);
    }
}
