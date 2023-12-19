package pl.puccini.cineflix.domain.user.viewingHistory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.episode.repository.EpisodeRepository;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.viewingHistory.ViewingHistoryFactory;
import pl.puccini.cineflix.domain.user.viewingHistory.dto.WatchedItemDto;
import pl.puccini.cineflix.domain.user.viewingHistory.model.ViewingHistory;
import pl.puccini.cineflix.domain.user.viewingHistory.repository.ViewingHistoryRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserViewingHistoryServiceTest {

    @Mock
    private ViewingHistoryRepository viewingHistoryRepository;
    @Mock
    private EpisodeRepository episodeRepository;
    @Mock
    private ViewingHistoryFactory viewingHistoryFactory;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserViewingHistoryService userViewingHistoryService;

    @Test
    void whenSaveEpisodeToViewingHistory_thenSavesSuccessfully() {
        User user = new User();
        Episode episode = new Episode();
        when(userRepository.findUserById(1L)).thenReturn(user);
        when(episodeRepository.findEpisodeById(1L)).thenReturn(episode);
        when(viewingHistoryFactory.createForEpisode(user, episode)).thenReturn(new ViewingHistory());

        userViewingHistoryService.saveEpisodeToViewingHistory(1L, 1L);

        verify(viewingHistoryRepository).save(any(ViewingHistory.class));
    }

    @Test
    void whenGetWatchedItems_thenReturnListOfItems() {
        when(viewingHistoryRepository.findByUserId(1L)).thenReturn(Collections.singletonList(new ViewingHistory()));

        List<WatchedItemDto> result = userViewingHistoryService.getWatchedItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void whenRemoveUserAndAllViewingHistory_thenRemovesSuccessfully() {
        when(viewingHistoryRepository.findByUserId(1L)).thenReturn(Collections.singletonList(new ViewingHistory()));

        userViewingHistoryService.removeUserAndAllViewingHistory(1L);

        verify(viewingHistoryRepository).deleteAll(anyList());
    }

}