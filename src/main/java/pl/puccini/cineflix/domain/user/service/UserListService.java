package pl.puccini.cineflix.domain.user.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.model.UserList;
import pl.puccini.cineflix.domain.user.repository.UserListRepository;
import pl.puccini.cineflix.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserListService {

    private final UserListRepository userListRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;

    public UserListService(UserListRepository userListRepository, UserRepository userRepository, MovieRepository movieRepository, SeriesRepository seriesRepository) {
        this.userListRepository = userListRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
    }

    public List<Movie> getUserMovies(Long userId) {
        List<UserList> userLists = userListRepository.findByUserId(userId);
        List<Movie> movies = new ArrayList<>();

        for (UserList userList : userLists) {
            Movie movieByImdbId = movieRepository.findMovieByImdbId(userList.getImdbId());
            if (movieByImdbId != null) {
                movies.add(movieByImdbId);
            }
        }
        return movies;
    }

    public List<Series> getUserSeries(Long userId) {
        List<UserList> userLists = userListRepository.findByUserId(userId);
        List<Series> series = new ArrayList<>();

        for (UserList userList : userLists) {
            Series seriesByImdbId = seriesRepository.findSeriesByImdbId(userList.getImdbId());
            if (seriesByImdbId != null) {
                series.add(seriesByImdbId);
            }
        }
        return series;
    }

    public boolean isOnList(Long userId , String imdbId) {
        return userListRepository.existsByUserIdAndImdbId(userId, imdbId);
    }

    public void addItemToList(Long userId, String imdbId) {
        if (!userListRepository.existsByUserIdAndImdbId(userId, imdbId)) {
            User user = userRepository.findUserById(userId);
            UserList item = new UserList();
            item.setUser(user);
            item.setImdbId(imdbId);
            userListRepository.save(item);
        }
    }

    public void removeItemFromList(Long userId, String imdbId) {
        userListRepository.deleteByUserIdAndImdbId(userId, imdbId);
    }

    public List<UserList> getUserList(Long userId) {
        return userListRepository.findByUserId(userId);
    }
}
