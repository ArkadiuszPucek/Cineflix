package pl.puccini.cineflix.domain.movie;

import org.springframework.stereotype.Component;
import pl.puccini.cineflix.domain.genre.GenreRepository;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;

@Component
public class MovieFactory {
    private final GenreRepository genreRepository;

    public MovieFactory(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Movie createMovie(MovieDto movieDto, Boolean isPromoted) {
        Movie movie = new Movie();
        updateMovieWithDto(movie, movieDto);
        if (isPromoted != null) {
            movie.setPromoted(isPromoted);
        }
        return movie;
    }

    public void updateMovieWithDto(Movie movie, MovieDto movieDto) {
        movie.setImdbId(movieDto.getImdbId());
        movie.setTitle(movieDto.getTitle());
        movie.setReleaseYear(movieDto.getReleaseYear());
        movie.setImageUrl(movieDto.getImageUrl());
        movie.setBackgroundImageUrl(movieDto.getBackgroundImageUrl());
        movie.setMediaUrl(movieDto.getMediaUrl());
        movie.setTimeline(movieDto.getTimeline());
        movie.setAgeLimit(movieDto.getAgeLimit());
        movie.setDescription(movieDto.getDescription());
        movie.setStaff(movieDto.getStaff());
        movie.setDirectedBy(movieDto.getDirectedBy());
        movie.setLanguages(movieDto.getLanguages());
        movie.setGenre(genreRepository.findByGenreTypeIgnoreCase(movieDto.getGenre()));
        movie.setImdbRating(movieDto.getImdbRating());
        movie.setPromoted(movieDto.isPromoted());
        movie.setImdbUrl("https://www.imdb.com/title/" + movieDto.getImdbId());
    }
}
