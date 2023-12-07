package pl.puccini.cineflix.domain.series.service;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.SeriesAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreRepository;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.cineflix.domain.series.dto.seasonDto.SeasonDto;
import pl.puccini.cineflix.domain.series.dto.seasonDto.SeasonDtoMapper;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDtoMapper;
import pl.puccini.cineflix.domain.series.model.Episode;
import pl.puccini.cineflix.domain.series.model.Season;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.model.SeriesPromoBox;
import pl.puccini.cineflix.domain.series.repository.SeasonRepository;
import pl.puccini.cineflix.domain.series.repository.SeriesPromoBoxRepository;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.model.UserRating;
import pl.puccini.cineflix.domain.user.repository.UserRatingRepository;
import pl.puccini.cineflix.domain.user.service.UserListService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final IMDbApiService imdbApiService;
    private final GenreRepository genreRepository;
    private final UserListService userListService;
    private final GenreService genreService;
    private final EpisodeService episodeService;
    private final UserRatingRepository userRatingRepository;
    private final SeriesPromoBoxRepository seriesPromoBoxRepository;

    public SeriesService(SeriesRepository seriesRepository, SeasonRepository seasonRepository, IMDbApiService imdbApiService, GenreRepository genreRepository, UserListService userListService, GenreService genreService, EpisodeService episodeService, UserRatingRepository userRatingRepository, SeriesPromoBoxRepository seriesPromoBoxRepository) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.imdbApiService = imdbApiService;
        this.genreRepository = genreRepository;
        this.userListService = userListService;
        this.genreService = genreService;
        this.episodeService = episodeService;
        this.userRatingRepository = userRatingRepository;
        this.seriesPromoBoxRepository = seriesPromoBoxRepository;
    }


    public List<SeriesDto> findAllPromotedSeries() {
        return seriesRepository.findAllByPromotedIsTrue().stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public List<SeriesDto> getSeriesByImdbId(String imdbId) {
        return seriesRepository.findAllByImdbId(imdbId).stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }




    public List<SeriesDto> getSeriesByGenre(String genre, Long userId) {
        Genre genreByType = genreService.getGenreByType(genre);
        List<SeriesDto> seriesDtos = seriesRepository.findAllByGenre(genreByType).stream()
                .map(SeriesDtoMapper::map)
                .toList();
        seriesDtos.forEach(serie -> {
            serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId()));
            serie.setUserRating(getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
        });
        return seriesDtos;
    }

    public SeriesDto findSeriesByTitle(String title, Long userId) {
        Series series = seriesRepository.findByTitleIgnoreCase(title);
        if (series == null) {
            return null;
        }
        SeriesDto mappedSeries = SeriesDtoMapper.map(series);
        mappedSeries.setOnUserList(userListService.isOnList(userId, series.getImdbId()));
        mappedSeries.setUserRating(getCurrentUserRatingForSeries(mappedSeries.getImdbId(), userId).orElse(null));
        return mappedSeries;
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

        Set<Long> watchedEpisodeIds = episodeService.getWatchedEpisodesIds(userId);
        List<Episode> episodes = season.getEpisodes();
        List<EpisodeDto> episodeDtos = new ArrayList<>();

        for (Episode episode : episodes) {
            EpisodeDto episodeDto = EpisodeDtoMapper.map(episode);
            episodeDto.setWatched(watchedEpisodeIds.contains(episode.getId()));
            episodeDtos.add(episodeDto);
        }

        return episodeDtos;
    }

    public boolean existsByImdbId(String imdbId) {
        return seriesRepository.existsByImdbId(imdbId);
    }

    public Series addSeriesManual(SeriesDto seriesDto) throws IOException, InterruptedException {
        Series series = new Series();
        series.setImdbId(seriesDto.getImdbId());
        series.setTitle(seriesDto.getTitle());
        series.setReleaseYear(seriesDto.getReleaseYear());
        series.setImageUrl(seriesDto.getImageUrl());
        series.setBackgroundImageUrl(seriesDto.getBackgroundImageUrl());
        series.setDescription(seriesDto.getDescription());
        series.setStaff(seriesDto.getStaff());
        series.setGenre(genreRepository.findByGenreTypeIgnoreCase(seriesDto.getGenre()));
        series.setPromoted(seriesDto.isPromoted());
        series.setAgeLimit(seriesDto.getAgeLimit());
        series.setImdbRating(seriesDto.getImdbRating());
        series.setImdbUrl("https://www.imdb.com/title/" + seriesDto.getImdbId());
        series.setSeasonsCount(seriesDto.getSeasonsCount());
        seriesRepository.save(series);
        return series;
    }

    public Series addSeriesByApi(SeriesDto seriesDto) throws IOException, InterruptedException {
        SeriesDto seriesApiDto = imdbApiService.fetchIMDbDataForSeries(seriesDto.getImdbId());
        Series series = new Series();

        series.setImdbId(seriesDto.getImdbId());
        series.setTitle(seriesApiDto.getTitle());
        series.setReleaseYear(seriesApiDto.getReleaseYear());
        series.setImageUrl(seriesApiDto.getImageUrl());
        series.setBackgroundImageUrl(seriesApiDto.getBackgroundImageUrl());
        series.setDescription(seriesApiDto.getDescription());
        series.setStaff(seriesApiDto.getStaff());
        series.setGenre(genreRepository.findByGenreTypeIgnoreCase(seriesApiDto.getGenre()));
        series.setPromoted(seriesDto.isPromoted());
        series.setAgeLimit(seriesApiDto.getAgeLimit());
        series.setImdbRating(seriesApiDto.getImdbRating());
        series.setImdbUrl(seriesApiDto.getImdbUrl());
        series.setSeasonsCount(seriesApiDto.getSeasonsCount());

        seriesRepository.save(series);

        imdbApiService.addSeasonsAndEpisodesToSeries(series, seriesApiDto.getImdbId());
        return series;
    }



    public List<SeriesDto> findAllSeriesInService() {
        return seriesRepository.findAll().stream()
                .map(series -> {
                    SeriesDto seriesDto = SeriesDtoMapper.map(series);
                    int rateUpCount = userRatingRepository.countBySeriesImdbIdAndUpvote(series.getImdbId(), true);
                    int rateDownCount = userRatingRepository.countBySeriesImdbIdAndUpvote(series.getImdbId(), false);
                    seriesDto.setRateUpCount(rateUpCount);
                    seriesDto.setRateDownCount(rateDownCount);
                    return seriesDto;
                })
                .toList();
    }


    public SeriesDto findSeriesByImdbId(String imdbId) {
        Series seriesByImdbId = seriesRepository.findSeriesByImdbId(imdbId);
        return SeriesDtoMapper.map(seriesByImdbId);
    }

    public Series findSeriesByImdbIdSeriesType(String imdbId) {
        return seriesRepository.findSeriesByImdbId(imdbId);
    }


    public void updateSeries(SeriesDto seriesDto) {
        Series existingSeries = seriesRepository.findSeriesByImdbId(seriesDto.getImdbId());

        if (existingSeries == null) {
            throw new SeriesNotFoundException("Nie znaleziono serialu o ID: " + seriesDto.getImdbId());
        }
        existingSeries.setTitle(seriesDto.getTitle());
        existingSeries.setReleaseYear(seriesDto.getReleaseYear());
        existingSeries.setImageUrl(seriesDto.getImageUrl());
        existingSeries.setBackgroundImageUrl(seriesDto.getBackgroundImageUrl());
        existingSeries.setDescription(seriesDto.getDescription());
        existingSeries.setStaff(seriesDto.getStaff());
        existingSeries.setGenre(genreRepository.findByGenreTypeIgnoreCase(seriesDto.getGenre()));
        existingSeries.setPromoted(seriesDto.isPromoted());
        existingSeries.setAgeLimit(seriesDto.getAgeLimit());
        existingSeries.setImdbRating(seriesDto.getImdbRating());
        seriesRepository.save(existingSeries);
    }


    public boolean deleteSeriesByImdbId(String imdbId) {
        Series seriesByImdbId = seriesRepository.findSeriesByImdbId(imdbId);
        if (seriesByImdbId != null) {
            seriesRepository.delete(seriesByImdbId);
            return true;
        }
        return false;
    }

    public Series addSeriesIfNotExist(SeriesDto seriesDto) throws IOException, InterruptedException {
        if (existsByImdbId(seriesDto.getImdbId())) {
            throw new SeriesAlreadyExistsException("Serial o podanym IMDb id istnieje w serwisie!");
        }
        return addSeriesByApi(seriesDto);
    }

    public String getNormalizedSeriesTitle(String title) {
        return title.toLowerCase().replace(" ", "-");
    }

    public Optional<Boolean> getCurrentUserRatingForSeries(String imdbId, Long userId) {
        return userRatingRepository.findBySeriesImdbIdAndUserId(imdbId, userId)
                .map(UserRating::isUpvote);
    }


    public List<SeriesDto> getSeriesPromoBox(Long userId) {
        SeriesPromoBox promoBox = seriesPromoBoxRepository.findTopByOrderByIdDesc();
        if (promoBox == null) {
            return Collections.emptyList();
        }

        String[] imdbIds = promoBox.getImdbIds().split(",");
        return Arrays.stream(imdbIds)
                .flatMap(imdbId -> getSeriesByImdbId(imdbId).stream())
                .peek(serie -> {
                    serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId()));
                    serie.setUserRating(getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
                })
                .collect(Collectors.toList());
    }

    public String getSeriesPromoBoxTitle() {
        SeriesPromoBox promoBox = seriesPromoBoxRepository.findTopByOrderByIdDesc();
        if (promoBox != null) {
            return promoBox.getSeriesPromoBoxTitle();
        } else {
            return "Trending series";
        }
    }

    public void updateSeriesPromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
        List<String> allImdbIds = Arrays.asList(imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        List<String> validImdbIds = new ArrayList<>();

        for (String imdbId : allImdbIds) {
            if (seriesExists(imdbId)) {
                validImdbIds.add(imdbId);
            } else {
                throw new SeriesNotFoundException("Series not found");
            }
        }

        String joinedImdbIds = String.join(",", validImdbIds);

        SeriesPromoBox seriesPromoBox = new SeriesPromoBox();
        seriesPromoBox.setSeriesPromoBoxTitle(title);
        seriesPromoBox.setImdbIds(joinedImdbIds);
        seriesPromoBoxRepository.save(seriesPromoBox);

    }

    private boolean seriesExists(String imdbId) {
        return seriesRepository.existsByImdbId(imdbId);
    }
}
