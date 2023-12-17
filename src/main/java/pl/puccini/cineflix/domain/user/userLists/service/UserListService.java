package pl.puccini.cineflix.domain.user.userLists.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userLists.model.UserList;
import pl.puccini.cineflix.domain.user.userLists.repository.UserListRepository;

import java.util.List;

@Service
public class UserListService {

    private final UserListRepository userListRepository;
    private final UserRepository userRepository;
    private final UserMoviesListService userMoviesListService;
    private final UserSeriesListService userSeriesListService;

    public UserListService(UserListRepository userListRepository, UserRepository userRepository, UserMoviesListService userMoviesListService, UserSeriesListService userSeriesListService) {
        this.userListRepository = userListRepository;
        this.userRepository = userRepository;
        this.userMoviesListService = userMoviesListService;
        this.userSeriesListService = userSeriesListService;
    }

    public List<MovieDto> getUserMovies(Long userId) {
        return userMoviesListService.getUserMovies(userId);
    }

    public List<SeriesDto> getUserSeries(Long userId) {
        return userSeriesListService.getUserSeries(userId);
    }

    public boolean isOnList(Long userId, String imdbId) {
        return userListRepository.existsByUserIdAndImdbId(userId, imdbId);
    }

    public void addItemToList(Long userId, String imdbId) {
        if (!isOnList(userId, imdbId)) {
            User user = userRepository.findUserById(userId);
            UserList item = new UserList();
            item.setUser(user);
            item.setImdbId(imdbId);
            userListRepository.save(item);
        }
    }

    @Transactional
    public void removeItemFromList(Long userId, String imdbId) {
        userListRepository.deleteByUserIdAndImdbId(userId, imdbId);
    }

    @Transactional
    public void removeUserAndAllItems(Long userId) {
        List<UserList> byUserId = userListRepository.findByUserId(userId);
        userListRepository.deleteAll(byUserId);
        userListRepository.deleteUserById(userId);
    }
}
