package pl.puccini.cineflix.domain.user.userLists.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.userLists.model.UserList;
import pl.puccini.cineflix.domain.user.userLists.repository.UserListRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserListServiceTest {

    @Mock
    private UserListRepository userListRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMoviesListService userMoviesListService;
    @Mock
    private UserSeriesListService userSeriesListService;

    @InjectMocks
    private UserListService userListService;

    private final Long userId = 1L;
    private final String imdbId = "tt1234567";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
    }

    @Test
    void whenGettingUserMovies_thenReturnsMoviesList() {
        List<MovieDto> mockMovies = Collections.singletonList(new MovieDto());
        when(userMoviesListService.getUserMovies(userId)).thenReturn(mockMovies);

        List<MovieDto> result = userListService.getUserMovies(userId);
        assertEquals(mockMovies, result);
    }

    @Test
    void whenGettingUserSeries_thenReturnsSeriesList() {
        List<SeriesDto> mockSeries = Collections.singletonList(new SeriesDto());
        when(userSeriesListService.getUserSeries(userId)).thenReturn(mockSeries);

        List<SeriesDto> result = userListService.getUserSeries(userId);
        assertEquals(mockSeries, result);
    }

    @Test
    void whenCheckingIfItemIsOnList_andItemIsOnList_thenReturnsTrue() {
        when(userListRepository.existsByUserIdAndImdbId(userId, imdbId)).thenReturn(true);

        assertTrue(userListService.isOnList(userId, imdbId));
    }

    @Test
    void whenCheckingIfItemIsOnList_andItemIsNotOnList_thenReturnsFalse() {
        when(userListRepository.existsByUserIdAndImdbId(userId, imdbId)).thenReturn(false);

        assertFalse(userListService.isOnList(userId, imdbId));
    }

    @Test
    void whenAddingItemToList_andItemNotExists_thenItemIsAdded() {
        when(userListRepository.existsByUserIdAndImdbId(userId, imdbId)).thenReturn(false);
        when(userRepository.findUserById(userId)).thenReturn(user);

        userListService.addItemToList(userId, imdbId);

        verify(userListRepository).save(any(UserList.class));
    }

    @Test
    void whenAddingItemToList_andItemAlreadyExists_thenItemIsNotAdded() {
        when(userListRepository.existsByUserIdAndImdbId(userId, imdbId)).thenReturn(true);

        userListService.addItemToList(userId, imdbId);

        verify(userListRepository, never()).save(any(UserList.class));
    }

    @Test
    void whenRemovingItemFromList_thenItemIsRemoved() {
        userListService.removeItemFromList(userId, imdbId);

        verify(userListRepository).deleteByUserIdAndImdbId(userId, imdbId);
    }

    @Test
    void whenRemovingAllItemsForUser_thenAllItemsAreRemoved() {
        when(userListRepository.findByUserId(userId)).thenReturn(Collections.singletonList(new UserList()));

        userListService.removeUserAndAllItems(userId);

        verify(userListRepository).deleteAll(anyList());
        verify(userListRepository).deleteUserById(userId);
    }
}