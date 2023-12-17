package pl.puccini.cineflix.domain.user.viewingHistory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puccini.cineflix.domain.exceptions.MovieNotFoundException;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.repository.MovieRepository;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.episode.repository.EpisodeRepository;
import pl.puccini.cineflix.domain.user.viewingHistory.dto.WatchedItemDto;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.userDetails.repository.UserRepository;
import pl.puccini.cineflix.domain.user.viewingHistory.model.ViewingHistory;
import pl.puccini.cineflix.domain.user.viewingHistory.ViewingHistoryFactory;
import pl.puccini.cineflix.domain.user.viewingHistory.repository.ViewingHistoryRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserViewingHistoryService {
    private final ViewingHistoryRepository viewingHistoryRepository;
    private final EpisodeRepository episodeRepository;
    private final MovieRepository movieRepository;
    private final ViewingHistoryFactory viewingHistoryFactory;
    private final UserRepository userRepository;

    public UserViewingHistoryService(ViewingHistoryRepository viewingHistoryRepository, EpisodeRepository episodeRepository, MovieRepository movieRepository, ViewingHistoryFactory viewingHistoryFactory, UserRepository userRepository) {
        this.viewingHistoryRepository = viewingHistoryRepository;
        this.episodeRepository = episodeRepository;
        this.movieRepository = movieRepository;
        this.viewingHistoryFactory = viewingHistoryFactory;
        this.userRepository = userRepository;
    }


    @Transactional
    public void saveEpisodeToViewingHistory(Long userId, Long episodeId) {
        User user = userRepository.findUserById(userId);
        Episode episode = episodeRepository.findEpisodeById(episodeId);
        ViewingHistory history = viewingHistoryFactory.createForEpisode(user, episode);
        viewingHistoryRepository.save(history);
    }


    @Transactional
    public void saveMovieToViewingHistory(Long userId, String imdbId) {
        User user = userRepository.findUserById(userId);
        Movie movie = movieRepository.findMovieByImdbId(imdbId).orElseThrow(() -> new MovieNotFoundException("Movie not found"));
        ViewingHistory history = viewingHistoryFactory.createForMovie(user, movie);
        viewingHistoryRepository.save(history);
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
        return viewingHistoryRepository.findByUserId(userId).stream()
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
