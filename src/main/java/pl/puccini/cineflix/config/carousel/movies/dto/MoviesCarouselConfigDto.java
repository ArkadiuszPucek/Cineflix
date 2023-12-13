package pl.puccini.cineflix.config.carousel.movies.dto;

import lombok.Getter;
import lombok.Setter;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;

import java.util.List;

@Getter
@Setter
public class MoviesCarouselConfigDto {

    private String genre;
    private List<MovieDto> movies;
    private boolean isActive;

}