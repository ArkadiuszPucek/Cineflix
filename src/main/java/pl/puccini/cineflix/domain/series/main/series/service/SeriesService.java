package pl.puccini.cineflix.domain.series.main.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.IncorrectTypeException;
import pl.puccini.cineflix.domain.exceptions.SeriesAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.genre.GenreFacade;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFacade;
import pl.puccini.cineflix.domain.series.main.series.SeriesFactory;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDtoMapper;
import pl.puccini.cineflix.domain.series.main.season.seasonDto.SeasonDto;
import pl.puccini.cineflix.domain.series.main.season.seasonDto.SeasonDtoMapper;
import pl.puccini.cineflix.domain.series.main.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDtoMapper;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.season.model.Season;
import pl.puccini.cineflix.domain.series.main.season.repository.SeasonRepository;
import pl.puccini.cineflix.domain.user.userLists.UserListFacade;
import pl.puccini.cineflix.domain.user.userRatings.UserRatingFacade;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final IMDbApiService imdbApiService;
    private final GenreFacade genreFacade;
    private final EpisodeFacade episodeFacade;
    private final UserListFacade userListFacade;
    private final SeriesFactory seriesFactory;
    private final UserRatingFacade userRatingFacade;

    public SeriesService(SeriesRepository seriesRepository, SeasonRepository seasonRepository, IMDbApiService imdbApiService, GenreFacade genreFacade, EpisodeFacade episodeFacade, UserListFacade userListFacade, SeriesFactory seriesFactory, UserRatingFacade userRatingFacade) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.imdbApiService = imdbApiService;
        this.genreFacade = genreFacade;
        this.episodeFacade = episodeFacade;
        this.userListFacade = userListFacade;
        this.seriesFactory = seriesFactory;
        this.userRatingFacade = userRatingFacade;
    }

    public Series addSeriesByApiIfNotExist(SeriesDto seriesDto) throws IOException, InterruptedException {
        if (existsByImdbId(seriesDto.getImdbId())) {
            throw new SeriesAlreadyExistsException("The series with the given IMDb id exists on the website!");
        }
        return addSeriesByApi(seriesDto);
    }

    public Series addSeriesManualIfNotExist(SeriesDto seriesDto) {
        if (existsByImdbId(seriesDto.getImdbId())) {
            throw new SeriesAlreadyExistsException("The series with the given IMDb id exists on the website!");
        }
        Series series = seriesFactory.createSeries(seriesDto, null);
        seriesRepository.save(series);
        return series;
    }

    public boolean existsByImdbId(String imdbId) {
        return seriesRepository.existsByImdbId(imdbId);
    }

    public Series addSeriesByApi(SeriesDto seriesDto) throws IOException, InterruptedException {
        String type = imdbApiService.fetchIMDbForTypeCheck(seriesDto.getImdbId());
        if (type.equals("show")) {
            SeriesDto seriesApiDto = imdbApiService.fetchIMDbDataForSeries(seriesDto.getImdbId());
            Series series = seriesFactory.createSeries(seriesApiDto, seriesDto.isPromoted());
            seriesRepository.save(series);
            imdbApiService.fetchIMDbDataAndAddSeasonsAndEpisodesToSeries(seriesApiDto.getImdbId());
            return series;
        }else {
            throw new IncorrectTypeException("Incorrect imdbId type for series");
        }
    }

    public void updateSeries(SeriesDto seriesDto){
        Series existingSeries = seriesRepository.findSeriesByImdbId(seriesDto.getImdbId())
                .orElseThrow(() -> new SeriesNotFoundException("Series not found: " + seriesDto.getImdbId()));

        seriesFactory.updateSeriesWithDto(existingSeries, seriesDto);
        seriesRepository.save(existingSeries);
    }

    public boolean deleteSeriesByImdbId(String imdbId) {
        return seriesRepository.findSeriesByImdbId(imdbId)
                .map(series -> {
                    seriesRepository.delete(series);
                    return true;
                })
                .orElse(false);
    }

    public List<SeriesDto> getSeriesByGenre(String genre, Long userId) {
        Genre genreByType = genreFacade.getGenreByType(genre);
        List<SeriesDto> seriesDtos = seriesRepository.findAllByGenre(genreByType).stream()
                .map(SeriesDtoMapper::map)
                .toList();
        seriesDtos.forEach(serie -> {
            serie.setOnUserList(userListFacade.isOnList(userId, serie.getImdbId()));
            serie.setUserRating(userRatingFacade.getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
        });
        return seriesDtos;
    }

    public SeriesDto findSeriesByTitle(String title, Long userId) {
        Series series = seriesRepository.findByTitleIgnoreCase(title);
        if (series == null) {
            return null;
        }
        SeriesDto mappedSeries = SeriesDtoMapper.map(series);
        mappedSeries.setOnUserList(userListFacade.isOnList(userId, series.getImdbId()));
        mappedSeries.setUserRating(userRatingFacade.getCurrentUserRatingForSeries(mappedSeries.getImdbId(), userId).orElse(null));
        return mappedSeries;
    }

    public List<SeriesDto> findAllSeriesInService(Long userId) {
        List<SeriesDto> allSeriesDto = seriesRepository.findAll().stream()
                .map(series -> {
                    SeriesDto seriesDto = SeriesDtoMapper.map(series);
                    int rateUpCount = userRatingFacade.getRateUpCountForSeries(series);
                    int rateDownCount = userRatingFacade.getRateDownCountForSeries(series);
                    seriesDto.setRateUpCount(rateUpCount);
                    seriesDto.setRateDownCount(rateDownCount);
                    return seriesDto;
                })
                .toList();

        allSeriesDto.forEach(serie -> {
            serie.setOnUserList(userListFacade.isOnList(userId, serie.getImdbId()));
            serie.setUserRating(userRatingFacade.getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
        });
        return allSeriesDto;
    }

    public List<SeriesDto> searchSeries(String query) {
        String loweredQuery = query.toLowerCase();
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        return seriesRepository.findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCase(loweredQuery, loweredQuery).stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public SeriesDto findSeriesByImdbId(String imdbId) {
        Series seriesByImdbId = seriesRepository.findSeriesByImdbId(imdbId)
                .orElseThrow(() -> new SeriesNotFoundException("Series not found: " + imdbId));
        return SeriesDtoMapper.map(seriesByImdbId);
    }

    public Series findSeriesByImdbIdSeriesType(String imdbId) {
        return seriesRepository.findSeriesByImdbId(imdbId)
                .orElseThrow(() -> new SeriesNotFoundException("Series not found: " + imdbId));
    }

    public String getNormalizedSeriesTitle(String title) {
        return title.toLowerCase().replace(" ", "-");
    }

    public List<SeasonDto> getSeasonsForSeries(String imdbId) {
        List<Season> seasons = seasonRepository.findSeasonsBySeriesImdbId(imdbId);

        return seasons.stream()
                .map(SeasonDtoMapper::map)
                .collect(Collectors.toList());
    }

    public List<EpisodeDto> getEpisodesForSeason(Long seasonId, Long userId) {
        Season season = seasonRepository.findById(seasonId).orElse(null);
        if (season == null) {
            return Collections.emptyList();
        }

        Set<Long> watchedEpisodeIds = episodeFacade.getWatchedEpisodesIds(userId);
        List<Episode> episodes = season.getEpisodes();
        List<EpisodeDto> episodeDtos = new ArrayList<>();

        for (Episode episode : episodes) {
            EpisodeDto episodeDto = EpisodeDtoMapper.map(episode);
            episodeDto.setWatched(watchedEpisodeIds.contains(episode.getId()));
            episodeDtos.add(episodeDto);
        }
        return episodeDtos;
    }

    public int getNumberOfSeriesByGenre(Genre genreType) {
        return seriesRepository.countSeriesByGenre(genreType);
    }
}
