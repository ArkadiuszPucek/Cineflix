package pl.puccini.cineflix.domain.movie.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MoviesCarouselConfigDto {

    private String genre;
    private List<MovieDto> movies;
    private boolean isActive;

}