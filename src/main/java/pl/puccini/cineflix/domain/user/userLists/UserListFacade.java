package pl.puccini.cineflix.domain.user.userLists;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.user.userLists.service.UserListService;

import java.util.List;

@Service
public class UserListFacade {
    private final UserListService userListService;

    public UserListFacade(UserListService userListService) {
        this.userListService = userListService;
    }

    public List<MovieDto> getUserMovies(Long userId) {
        return userListService.getUserMovies(userId);
    }

    public List<SeriesDto> getUserSeries(Long userId) {
        return userListService.getUserSeries(userId);
    }

    public void addItemToList(Long userId, String imdbId) {
        userListService.addItemToList(userId, imdbId);
    }

    public void removeItemFromList(Long userId, String imdbId) {
        userListService.removeItemFromList(userId, imdbId);
    }

    public boolean isOnList(Long userId, String imdbId) {
        return userListService.isOnList(userId, imdbId);
    }
}
