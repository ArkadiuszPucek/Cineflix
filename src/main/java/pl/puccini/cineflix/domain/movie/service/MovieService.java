package pl.puccini.cineflix.domain.movie.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.MovieNotFoundException;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreRepository;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.dto.MovieDtoMapper;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.model.MoviesPromoBox;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.movie.repository.MoviesPromoBoxRepository;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.model.SeriesPromoBox;
import pl.puccini.cineflix.domain.user.model.UserRating;
import pl.puccini.cineflix.domain.user.repository.UserRatingRepository;
import pl.puccini.cineflix.domain.user.service.UserListService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final IMDbApiService imdbApiService;
    private final GenreRepository genreRepository;
    private final GenreService genreService;
    private final UserListService userListService;
    private final UserRatingRepository userRatingRepository;
    private final MoviesPromoBoxRepository moviesPromoBoxRepository;

    public MovieService(MovieRepository movieRepository, IMDbApiService imdbApiService, GenreRepository genreRepository, GenreService genreService, UserListService userListService, UserRatingRepository userRatingRepository, MoviesPromoBoxRepository moviesPromoBoxRepository) {
        this.movieRepository = movieRepository;
        this.imdbApiService = imdbApiService;
        this.genreRepository = genreRepository;
        this.genreService = genreService;
        this.userListService = userListService;
        this.userRatingRepository = userRatingRepository;
        this.moviesPromoBoxRepository = moviesPromoBoxRepository;
    }


    public List<MovieDto> findAllPromotedMovies() {
        return movieRepository.findAllByPromotedIsTrue().stream()
                .map(MovieDtoMapper::map)
                .toList();
    }

    public boolean existsByImdbId(String imdbId) {
        return movieRepository.existsByImdbId(imdbId);
    }

//    public void addMovieManual(MovieDto movieDto) {
//        Movie movie = new Movie();
//        movie.setImdbId(movieDto.getImdbId());
//        movie.setTitle(movieDto.getTitle());
//        movie.setReleaseYear(movieDto.getReleaseYear());
//        movie.setImageUrl(movieDto.getImageUrl());
//        movie.setBackgroundImageUrl(movieDto.getBackgroundImageUrl());
//        movie.setMediaUrl(movieDto.getMediaUrl());
//        movie.setTimeline(movieDto.getTimeline());
//        movie.setAgeLimit(movieDto.getAgeLimit());
//        movie.setDescription(movieDto.getDescription());
//        movie.setStaff(movieDto.getStaff());
//        movie.setDirectedBy(movieDto.getDirectedBy());
//        movie.setLanguages(movieDto.getLanguages());
//        movie.setGenre(genreRepository.findByGenreTypeIgnoreCase(movieDto.getGenre()));
//        movie.setImdbRating(movieDto.getImdbRating());
//        movie.setPromoted(movieDto.isPromoted());
//        movie.setImdbUrl("https://www.imdb.com/title/"+movieDto.getImdbId());
//        movieRepository.save(movie);
//    }
//
//    public Movie addMovieByApi(MovieDto movieDto) throws IOException, InterruptedException {
//        MovieDto movieApiDto = imdbApiService.fetchIMDbData(movieDto.getImdbId());
//        Movie movie = new Movie();
//        movie.setImdbId(movieApiDto.getImdbId());
//        movie.setTitle(movieApiDto.getTitle());
//        movie.setReleaseYear(movieApiDto.getReleaseYear());
//        movie.setImageUrl(movieApiDto.getImageUrl());
//        movie.setBackgroundImageUrl(movieApiDto.getBackgroundImageUrl());
//        movie.setMediaUrl(movieApiDto.getMediaUrl());
//        movie.setTimeline(movieApiDto.getTimeline());
//        movie.setAgeLimit(movieApiDto.getAgeLimit());
//        movie.setDescription(movieApiDto.getDescription());
//        movie.setStaff(movieApiDto.getStaff());
//        movie.setDirectedBy(movieApiDto.getDirectedBy());
//        movie.setLanguages(movieApiDto.getLanguages());
//        movie.setGenre(genreRepository.findByGenreTypeIgnoreCase(movieApiDto.getGenre()));
//        movie.setImdbRating(movieApiDto.getImdbRating());
//        movie.setPromoted(movieDto.isPromoted());
//        movie.setImdbUrl("https://www.imdb.com/title/"+movieDto.getImdbId());
//        movieRepository.save(movie);
//        return movie;
//    }

    public void addMovieManual(MovieDto movieDto) {
        Movie movie = mapMovieDtoToMovie(movieDto, null);
        movieRepository.save(movie);
    }

    public Movie addMovieByApi(MovieDto movieDto) throws IOException, InterruptedException {
        MovieDto movieApiDto = imdbApiService.fetchIMDbData(movieDto.getImdbId());
        Movie movie = mapMovieDtoToMovie(movieApiDto, movieDto.isPromoted());
        movieRepository.save(movie);
        return movie;
    }

    private Movie mapMovieDtoToMovie(MovieDto movieDto, Boolean isPromoted) {
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
        movie.setPromoted(isPromoted != null ? isPromoted : movieDto.isPromoted());
        movie.setImdbUrl("https://www.imdb.com/title/" + movieDto.getImdbId());
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
        moviesDtos.forEach(movie -> {
            movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId()));
            movie.setUserRating(getCurrentUserRatingForMovie(movie.getImdbId(), userId).orElse(null));
        });
        return moviesDtos;
    }

    public MovieDto findMovieByTitle(String title, Long userId) {
        Movie movie = movieRepository.findByTitleIgnoreCase(title);
        if (movie == null){
            return null;
        }
        MovieDto mappedMovie = MovieDtoMapper.map(movie);
        mappedMovie.setOnUserList(userListService.isOnList(userId, mappedMovie.getImdbId()));
        mappedMovie.setUserRating(getCurrentUserRatingForMovie(mappedMovie.getImdbId(), userId).orElse(null));
        return mappedMovie;
    }

    public List<MovieDto> findAllMoviesInService(){
        return movieRepository.findAll().stream()
                .map(movie -> {
                    MovieDto movieDto = MovieDtoMapper.map(movie);
                    int rateUpCount = userRatingRepository.countByMovieImdbIdAndUpvote(movie.getImdbId(), true);
                    int rateDownCount = userRatingRepository.countByMovieImdbIdAndUpvote(movie.getImdbId(), false);
                    movieDto.setRateUpCount(rateUpCount);
                    movieDto.setRateDownCount(rateDownCount);
                    return movieDto;
                })
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

    public Optional<Boolean> getCurrentUserRatingForMovie(String imdbId, Long userId) {
        return userRatingRepository.findByMovieImdbIdAndUserId(imdbId, userId)
                .map(UserRating::isUpvote);
    }

    public List<MovieDto> getMoviePromoBox(Long userId) {
        MoviesPromoBox promoBox = moviesPromoBoxRepository.findTopByOrderByIdDesc();
        if (promoBox == null) {
            return Collections.emptyList();
        }

        String[] imdbIds = promoBox.getImdbIds().split(",");
        return Arrays.stream(imdbIds)
                .flatMap(imdbId -> getMoviesByImdbId(imdbId).stream())
                .peek(movie -> {
                    movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId()));
                    movie.setUserRating(getCurrentUserRatingForMovie(movie.getImdbId(), userId).orElse(null));
                })
                .collect(Collectors.toList());
    }

    public String getMoviesPromoBoxTitle() {
        MoviesPromoBox promoBox = moviesPromoBoxRepository.findTopByOrderByIdDesc();
        if (promoBox != null) {
            return promoBox.getMoviesPromoBoxTitle();
        } else {
            return "Trending Movies";
        }
    }

    public void updateMoviePromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        List<String> allImdbIds = Arrays.asList(imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        List<String> validImdbIds = new ArrayList<>();

        for (String imdbId : allImdbIds) {
            if (seriesExists(imdbId)) {
                validImdbIds.add(imdbId);
            } else {
                throw new MovieNotFoundException("Movie not found");
            }
        }

        String joinedImdbIds = String.join(",", validImdbIds);

        MoviesPromoBox moviesPromoBox = new MoviesPromoBox();
        moviesPromoBox.setMoviesPromoBoxTitle(title);
        moviesPromoBox.setImdbIds(joinedImdbIds);
        moviesPromoBoxRepository.save(moviesPromoBox);

    }

    private boolean seriesExists(String imdbId) {
        return movieRepository.existsByImdbId(imdbId);
    }

}
