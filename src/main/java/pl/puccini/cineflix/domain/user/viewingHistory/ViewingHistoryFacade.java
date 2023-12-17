package pl.puccini.cineflix.domain.user.viewingHistory;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.user.viewingHistory.dto.WatchedItemDto;
import pl.puccini.cineflix.domain.user.viewingHistory.service.UserViewingHistoryService;

import java.util.List;
import java.util.Set;

@Service
public class ViewingHistoryFacade {
    private final UserViewingHistoryService userViewingHistoryService;

    public ViewingHistoryFacade(UserViewingHistoryService userViewingHistoryService) {
        this.userViewingHistoryService = userViewingHistoryService;
    }

    public void saveEpisodeToViewingHistory(Long userId, Long episodeId) {
        userViewingHistoryService.saveEpisodeToViewingHistory(userId, episodeId);
    }

    public void saveMovieToViewingHistory(Long userId, String imdbId) {
        userViewingHistoryService.saveMovieToViewingHistory(userId, imdbId);
    }

    public List<WatchedItemDto> getWatchedItems(Long userId) {
        return userViewingHistoryService.getWatchedItems(userId);
    }
    public Set<Long> getWatchedEpisodeIds(Long userId) {
        return userViewingHistoryService.getWatchedEpisodeIds(userId);
    }

    public void removeUserAndAllViewingHistory(Long userId){
        userViewingHistoryService.removeUserAndAllViewingHistory(userId);
    }




    }
