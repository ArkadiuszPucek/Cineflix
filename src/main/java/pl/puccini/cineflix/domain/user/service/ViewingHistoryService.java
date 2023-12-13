package pl.puccini.cineflix.domain.user.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.exceptions.UserNotFoundException;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.model.Episode;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.user.dto.WatchedItemDto;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.model.ViewingHistory;
import pl.puccini.cineflix.domain.user.repository.ViewingHistoryRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ViewingHistoryService {
    private final ViewingHistoryRepository viewingHistoryRepository;
    private final UserService userService;
    private final EpisodeService episodeService;
    private final MovieFacade movieFacade;

    public ViewingHistoryService(ViewingHistoryRepository viewingHistoryRepository, UserService userService, @Lazy EpisodeService episodeService, MovieFacade movieFacade) {
        this.viewingHistoryRepository = viewingHistoryRepository;
        this.userService = userService;
        this.episodeService = episodeService;
        this.movieFacade = movieFacade;
    }


    public void saveEpisodeToViewingHistory(Long userId, Long episodeId){
        try{
            User user = userService.findUserById(userId);
            Episode episode = episodeService.findEpisodeById(episodeId);

            ViewingHistory history = new ViewingHistory();
            history.setUser(user);
            history.setEpisode(episode);
            history.setViewedOn(LocalDateTime.now());
            viewingHistoryRepository.save(history);

        }catch (UserNotFoundException e){
            throw new UserNotFoundException("Nie znaleziono użytkownika");
        }catch (EpisodeNotFoundException e){
            throw new EpisodeNotFoundException("Nie znaleziono epizodu");
        }
    }


    public void saveMovieToViewingHistory(Long userId, String imdbId){
        try{
            User user = userService.findUserById(userId);
            Movie movie = movieFacade.getMovieByImdbId(imdbId);

            ViewingHistory history = new ViewingHistory();
            history.setUser(user);
            history.setMovie(movie);
            history.setViewedOn(LocalDateTime.now());
            viewingHistoryRepository.save(history);
        }catch (UserNotFoundException e){
            throw new UserNotFoundException("Nie znaleziono użytkownika");
        }catch (EpisodeNotFoundException e){
            throw new EpisodeNotFoundException("Nie znaleziono filmu");
        }
    }

    public List<WatchedItemDto> getWatchedItems(Long userId) {
        List<ViewingHistory> historyItems = viewingHistoryRepository.findByUserId(userId);

        return historyItems.stream()
                .map(this::mapToWatchedItemDto)
                .sorted(Comparator.comparing(WatchedItemDto::getViewedOn).reversed())
                .collect(Collectors.toList());
    }

    private WatchedItemDto mapToWatchedItemDto(ViewingHistory history) {
        WatchedItemDto dto = new WatchedItemDto();
        if (history.getMovie() != null) {
            Movie movie = history.getMovie();
            dto.setMovieImdbId(movie.getImdbId());
            dto.setTitle(movie.getTitle());
            dto.setImageUrl(movie.getImageUrl());
            dto.setType("movie");
            dto.setImageUrl(movie.getImageUrl());
        } else if (history.getEpisode() != null) {
            Episode episode = history.getEpisode();
            dto.setEpisodeId(episode.getId());
            dto.setTitle(episode.getEpisodeTitle());
            dto.setType("episode");
            dto.setImageUrl(episode.getImageUrl());
            dto.setSeasonNumber(episode.getSeason().getSeasonNumber());
            dto.setEpisodeNumber(episode.getEpisodeNumber());
        }
        dto.setViewedOn(history.getViewedOn());
        return dto;
    }

    public Set<Long> getWatchedEpisodeIds(Long userId) {
        List<ViewingHistory> userViewingHistories = viewingHistoryRepository.findByUserId(userId);

        return userViewingHistories.stream()
                .map(ViewingHistory::getEpisode)
                .filter(Objects::nonNull)
                .map(Episode::getId)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void removeUserAndAllViewingHistory(Long userId){
        List<ViewingHistory> viewingHistoryByUserId = viewingHistoryRepository.findByUserId(userId);
        viewingHistoryRepository.deleteAll(viewingHistoryByUserId);
        viewingHistoryRepository.deleteUserById(userId);
    }


}
