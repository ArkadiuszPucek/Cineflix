package pl.puccini.cineflix.domain.movie.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreRepository;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.dto.MovieDtoMapper;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.user.model.UserRating;
import pl.puccini.cineflix.domain.user.model.ViewingHistory;
import pl.puccini.cineflix.domain.user.repository.UserRatingRepository;
import pl.puccini.cineflix.domain.user.repository.ViewingHistoryRepository;
import pl.puccini.cineflix.domain.user.service.UserListService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final IMDbApiService imdbApiService;
    private final GenreRepository genreRepository;
    private final GenreService genreService;
    private final UserListService userListService;
    private final ViewingHistoryRepository viewingHistoryRepository;
    private final UserRatingRepository userRatingRepository;

    public MovieService(MovieRepository movieRepository, IMDbApiService imdbApiService, GenreRepository genreRepository, GenreService genreService, UserListService userListService, ViewingHistoryRepository viewingHistoryRepository, UserRatingRepository userRatingRepository) {
        this.movieRepository = movieRepository;
        this.imdbApiService = imdbApiService;
        this.genreRepository = genreRepository;
        this.genreService = genreService;
        this.userListService = userListService;
        this.viewingHistoryRepository = viewingHistoryRepository;
        this.userRatingRepository = userRatingRepository;
    }


    public List<MovieDto> findAllPromotedMovies() {
        return movieRepository.findAllByPromotedIsTrue().stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public boolean existsByImdbId(String imdbId) {
        return movieRepository.existsByImdbId(imdbId);
    }

    public void addMovieManual(MovieDto movieDto) throws IOException, InterruptedException {
        Movie movie = new Movie();
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
        movie.setImdbUrl("https://www.imdb.com/title/"+movieDto.getImdbId());
        movieRepository.save(movie);
    }

    public Movie addMovieByApi(MovieDto movieDto) throws IOException, InterruptedException {
        MovieDto movieApiDto = imdbApiService.fetchIMDbData(movieDto.getImdbId());
        Movie movie = new Movie();
        movie.setImdbId(movieApiDto.getImdbId());
        movie.setTitle(movieApiDto.getTitle());
        movie.setReleaseYear(movieApiDto.getReleaseYear());
        movie.setImageUrl(movieApiDto.getImageUrl());
        movie.setBackgroundImageUrl(movieApiDto.getBackgroundImageUrl());
        movie.setMediaUrl(movieApiDto.getMediaUrl());
        movie.setTimeline(movieApiDto.getTimeline());
        movie.setAgeLimit(movieApiDto.getAgeLimit());
        movie.setDescription(movieApiDto.getDescription());
        movie.setStaff(movieApiDto.getStaff());
        movie.setDirectedBy(movieApiDto.getDirectedBy());
        movie.setLanguages(movieApiDto.getLanguages());
        movie.setGenre(genreRepository.findByGenreTypeIgnoreCase(movieApiDto.getGenre()));
        movie.setImdbRating(movieApiDto.getImdbRating());
        movie.setPromoted(movieDto.isPromoted());
        movie.setImdbUrl("https://www.imdb.com/title/"+movieDto.getImdbId());
        movieRepository.save(movie);
        return movie;
    }

    public List<MovieDto> getMoviesByImdbId(String imdbId){
        return movieRepository.findAllByImdbId(imdbId).stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public List<MovieDto> getMovieByGenre(String genre, Long userId){
        Genre genreByType = genreService.getGenreByType(genre);
        List<MovieDto> moviesDtos = movieRepository.findAllByGenre(genreByType).stream()
                .map(MovieDtoMapper::map)
                .toList();
//        List<SeriesDto> series = seriesService.getSeriesByGenre(genreByType);
        moviesDtos.forEach(movie -> movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId())));
        return moviesDtos;

//        return movieRepository.findAllByGenre(genre).stream()
//                .map(MovieDtoMapper::map)
//                .toList();
    }

    public MovieDto findMovieByTitle(String title, Long userId) {
        Movie movie = movieRepository.findByTitleIgnoreCase(title);
        if (movie == null){
            return null;
        }
        MovieDto mappedMovie = MovieDtoMapper.map(movie);
        mappedMovie.setOnUserList(userListService.isOnList(userId, mappedMovie.getImdbId()));
        return mappedMovie;
    }

    public List<MovieDto> findAllMoviesInService(){
        return movieRepository.findAll().stream()
                .map(MovieDtoMapper::map)
                .toList();

    }

    public List<MovieDto> searchMovies(String query) {
        String loweredQuery = query.toLowerCase();
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        return movieRepository.findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCaseOrDirectedByContainingIgnoreCase(loweredQuery , loweredQuery , loweredQuery).stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public MovieDto findMovieByImdbId(String imdbId){
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId);
        return MovieDtoMapper.map(movieByImdbId);
    }

    public boolean updateMovie(MovieDto movieDto) {
        Movie existingMovie = movieRepository.findMovieByImdbId(movieDto.getImdbId());

        if (existingMovie != null) {
            existingMovie.setTitle(movieDto.getTitle());
            existingMovie.setReleaseYear(movieDto.getReleaseYear());
            existingMovie.setImageUrl(movieDto.getImageUrl());
            existingMovie.setBackgroundImageUrl(movieDto.getBackgroundImageUrl());
            existingMovie.setMediaUrl(movieDto.getMediaUrl());
            existingMovie.setTimeline(movieDto.getTimeline());
            existingMovie.setAgeLimit(movieDto.getAgeLimit());
            existingMovie.setDescription(movieDto.getDescription());
            existingMovie.setStaff(movieDto.getStaff());
            existingMovie.setDirectedBy(movieDto.getDirectedBy());
            existingMovie.setLanguages(movieDto.getLanguages());
            existingMovie.setGenre(genreRepository.findByGenreTypeIgnoreCase(movieDto.getGenre()));
            existingMovie.setImdbRating(movieDto.getImdbRating());
            existingMovie.setPromoted(movieDto.isPromoted());
            movieRepository.save(existingMovie);
            return true;
        } else {
            return false;
        }
    }

    public Movie getMovieByImdbId(String imdbId){
        return movieRepository.findMovieByImdbId(imdbId);
    }

    public String getFormattedMovieTitle(String imdbId){
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId);
        return movieByImdbId.getTitle().toLowerCase().replace(' ', '-');
    }

    public MovieDto findMovieByTitle(String title){
        Movie movie = movieRepository.findByTitleIgnoreCase(title);
        return MovieDtoMapper.map(movie);
    }

    public boolean deleteMovieByImdbId(String imdbId) {
        Movie movieByImdbId = movieRepository.findMovieByImdbId(imdbId);
        if (movieByImdbId != null) {
            movieRepository.delete(movieByImdbId);
            return true;
        }
        return false;
    }

//    public List<MovieDto> getWatchedMovies(Long userId) {
//        // Pobierz listę historii oglądania filmów dla danego użytkownika, posortowaną od najnowszej
//        List<ViewingHistory> movieHistoryList = viewingHistoryRepository.findByUserIdOrderByViewedOnDesc(userId);
//
//        // Mapuj na DTO
//        return movieHistoryList.stream()
//                .map(ViewingHistory::getMovie)
//                .distinct()
//                .map(MovieDtoMapper::map)
//                .collect(Collectors.toList());
//    }

    public Optional<Boolean> getCurrentUserRatingForMovie(String imdbId, Long userId) {
        return userRatingRepository.findByMovieImdbIdAndUserId(imdbId, userId)
                .map(UserRating::isUpvote);
    }


}
