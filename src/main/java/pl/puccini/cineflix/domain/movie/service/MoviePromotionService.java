package pl.puccini.cineflix.domain.movie.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.MovieNotFoundException;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.dto.MovieDtoMapper;
import pl.puccini.cineflix.domain.movie.model.MoviesPromoBox;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.movie.repository.MoviesPromoBoxRepository;
import pl.puccini.cineflix.domain.user.service.UserRatingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoviePromotionService {
    private final MoviesPromoBoxRepository moviesPromoBoxRepository;
    private final MovieRepository movieRepository;
    private final UserRatingService userRatingService;
    private final MovieFacade movieFacade;

    public MoviePromotionService(MoviesPromoBoxRepository moviesPromoBoxRepository, MovieRepository movieRepository, UserRatingService userRatingService, MovieFacade movieFacade) {
        this.moviesPromoBoxRepository = moviesPromoBoxRepository;
        this.movieRepository = movieRepository;
        this.userRatingService = userRatingService;
        this.movieFacade = movieFacade;
    }


    public List<MovieDto> findAllPromotedMovies() {
        return movieRepository.findAllByPromotedIsTrue().stream()
                .map(MovieDtoMapper::map)
                .collect(Collectors.toList());
    }
    public List<MovieDto> getMoviePromoBox(Long userId) {
        MoviesPromoBox promoBox = moviesPromoBoxRepository.findTopByOrderByIdDesc();
        if (promoBox == null) {
            return Collections.emptyList();
        }

        String[] imdbIds = promoBox.getImdbIds().split(",");
        return Arrays.stream(imdbIds)
                .map(movieRepository::findMovieByImdbId)
                .map(MovieDtoMapper::map)
                .peek(movieDto -> {
                    movieDto.setOnUserList(movieFacade.isMovieOnUserList(userId, movieDto.getImdbId()));
                    movieDto.setUserRating(userRatingService.getCurrentUserRatingForMovie(movieDto.getImdbId(), userId).orElse(null));
                })
                .collect(Collectors.toList());
    }

    public String getMoviesPromoBoxTitle() {
        MoviesPromoBox promoBox = moviesPromoBoxRepository.findTopByOrderByIdDesc();
        return promoBox != null ? promoBox.getMoviesPromoBoxTitle() : "Trending Movies";
    }

    public void updateMoviePromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        List<String> allImdbIds = Arrays.asList(imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        List<String> validImdbIds = new ArrayList<>();

        for (String imdbId : allImdbIds) {
            if (movieFacade.doesMovieExists(imdbId)) {
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
}
