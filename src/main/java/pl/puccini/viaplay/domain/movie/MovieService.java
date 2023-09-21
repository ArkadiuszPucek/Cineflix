package pl.puccini.viaplay.domain.movie;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDto> findAllPromotedMovies() {
        return movieRepository.findAllByPromotedIsTrue().stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

}
