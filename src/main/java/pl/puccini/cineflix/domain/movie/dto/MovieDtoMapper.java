package pl.puccini.cineflix.domain.movie.dto;

import pl.puccini.cineflix.domain.movie.model.Movie;

public class MovieDtoMapper {
    public static MovieDto map(Movie movie) {
        return new MovieDto(
                movie.getImdbId(),
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getImageUrl(),
                movie.getBackgroundImageUrl(),
                movie.getMediaUrl(),
                movie.getTimeline(),
                movie.getAgeLimit(),
                movie.getDescription(),
                movie.getStaff(),
                movie.getDirectedBy(),
                movie.getLanguages(),
                movie.getGenre().getGenreType(),
                movie.isPromoted(),
                movie.getImdbRating(),
                movie.getImdbUrl()
        );
    }
}
