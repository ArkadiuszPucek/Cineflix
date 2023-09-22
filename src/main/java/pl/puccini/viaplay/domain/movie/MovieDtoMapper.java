package pl.puccini.viaplay.domain.movie;

import pl.puccini.viaplay.domain.movie.dto.MovieDto;

public class MovieDtoMapper {
    public static MovieDto map(Movie movie) {
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getImageUrl(),
                movie.getMediaUrl(),
                movie.getImdbRating(),
                movie.getTimeline(),
                movie.getAgeLimit(),
                movie.getDescription(),
                movie.getCast(),
                movie.getDirectedBy(),
                movie.getLanguages(),
                movie.getGenre().getName(),
                movie.isPromoted()
        );
    }
}
