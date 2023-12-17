package pl.puccini.cineflix.domain.user.userLists.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.dto.MovieDtoMapper;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.user.userLists.model.UserList;
import pl.puccini.cineflix.domain.user.userLists.repository.UserListRepository;
import pl.puccini.cineflix.domain.user.userRatings.service.UserMovieRatingService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMoviesListService {
    private final MovieRepository movieRepository;
    private final UserListRepository userListRepository;
    private final UserMovieRatingService userMovieRatingService;

    public UserMoviesListService(MovieRepository movieRepository,
                            UserListRepository userListRepository,
                            UserMovieRatingService userMovieRatingService) {
        this.movieRepository = movieRepository;
        this.userListRepository = userListRepository;
        this.userMovieRatingService = userMovieRatingService;
    }


    public List<MovieDto> getUserMovies(Long userId) {
        List<UserList> userLists = userListRepository.findByUserId(userId);
        List<MovieDto> movies = new ArrayList<>();

        for (UserList userList : userLists) {
            movieRepository.findMovieByImdbId(userList.getImdbId())
                    .ifPresent(movie -> {
                        MovieDto mappedMovie = MovieDtoMapper.map(movie);
                        mappedMovie.setOnUserList(userListRepository.existsByUserIdAndImdbId(userId, mappedMovie.getImdbId()));
                        mappedMovie.setUserRating(userMovieRatingService.getCurrentUserRatingForMovie(mappedMovie.getImdbId(), userId).orElse(null));
                        movies.add(mappedMovie);
                    });
        }
        return movies;
    }
}
