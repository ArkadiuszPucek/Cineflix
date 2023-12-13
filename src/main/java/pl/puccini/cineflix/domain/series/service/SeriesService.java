package pl.puccini.cineflix.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.IncorrectTypeException;
import pl.puccini.cineflix.domain.exceptions.SeriesAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.imdb.IMDbApiService;
import pl.puccini.cineflix.domain.series.SeriesFactory;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.cineflix.domain.series.dto.seasonDto.SeasonDto;
import pl.puccini.cineflix.domain.series.dto.seasonDto.SeasonDtoMapper;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDtoMapper;
import pl.puccini.cineflix.domain.series.model.Episode;
import pl.puccini.cineflix.domain.series.model.Season;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.repository.SeasonRepository;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.repository.UserRatingRepository;
import pl.puccini.cineflix.domain.user.service.UserListService;
import pl.puccini.cineflix.domain.user.service.UserRatingService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final IMDbApiService imdbApiService;
    private final GenreService genreService;
    private final EpisodeService episodeService;
    private final UserRatingRepository userRatingRepository;
    private final UserRatingService userRatingService;
    private final UserListService userListService;
    private final SeriesFactory seriesFactory;

    public SeriesService(SeriesRepository seriesRepository, SeasonRepository seasonRepository, IMDbApiService imdbApiService, GenreService genreService, EpisodeService episodeService, UserRatingRepository userRatingRepository, UserRatingService userRatingService, UserListService userListService, SeriesFactory seriesFactory) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.imdbApiService = imdbApiService;
        this.genreService = genreService;
        this.episodeService = episodeService;
        this.userRatingRepository = userRatingRepository;
        this.userRatingService = userRatingService;
        this.userListService = userListService;
        this.seriesFactory = seriesFactory;
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
        Series existingSeries = seriesRepository.findSeriesByImdbId(seriesDto.getImdbId());
        if (existingSeries == null){
            throw new SeriesNotFoundException("Series not found:  " + seriesDto.getImdbId());
        }
        seriesFactory.updateSeriesWithDto(existingSeries, seriesDto);
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

    public List<SeriesDto> getSeriesByGenre(String genre, Long userId) {
        Genre genreByType = genreService.getGenreByType(genre);
        List<SeriesDto> seriesDtos = seriesRepository.findAllByGenre(genreByType).stream()
                .map(SeriesDtoMapper::map)
                .toList();
        seriesDtos.forEach(serie -> {
            serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId()));
            serie.setUserRating(userRatingService.getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
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
        mappedSeries.setUserRating(userRatingService.getCurrentUserRatingForSeries(mappedSeries.getImdbId(), userId).orElse(null));
        return mappedSeries;
    }

    public List<SeriesDto> findAllSeriesInService(Long userId) {
        List<SeriesDto> allSeriesDto = seriesRepository.findAll().stream()
                .map(series -> {
                    SeriesDto seriesDto = SeriesDtoMapper.map(series);
                    int rateUpCount = userRatingRepository.countBySeriesImdbIdAndUpvote(series.getImdbId(), true);
                    int rateDownCount = userRatingRepository.countBySeriesImdbIdAndUpvote(series.getImdbId(), false);
                    seriesDto.setRateUpCount(rateUpCount);
                    seriesDto.setRateDownCount(rateDownCount);
                    return seriesDto;
                })
                .toList();

        allSeriesDto.forEach(serie -> {
            serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId()));
            serie.setUserRating(userRatingService.getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
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
        Series seriesByImdbId = seriesRepository.findSeriesByImdbId(imdbId);
        return SeriesDtoMapper.map(seriesByImdbId);
    }

    public Series findSeriesByImdbIdSeriesType(String imdbId) {
        return seriesRepository.findSeriesByImdbId(imdbId);
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
//    public List<SeriesDto> getSeriesByImdbId(String imdbId) {
//        return seriesRepository.findAllByImdbId(imdbId).stream()
//                .map(SeriesDtoMapper::map)
//                .toList();
//    }
//    public List<SeriesDto> findAllPromotedSeries() {
//        return seriesRepository.findAllByPromotedIsTrue().stream()
//                .map(SeriesDtoMapper::map)
//                .toList();
//    }
//    public void updateSeries(SeriesDto seriesDto) {
//        Series existingSeries = seriesRepository.findSeriesByImdbId(seriesDto.getImdbId());
//
//        if (existingSeries == null) {
//            throw new SeriesNotFoundException("Nie znaleziono serialu o ID: " + seriesDto.getImdbId());
//        }
//        existingSeries.setTitle(seriesDto.getTitle());
//        existingSeries.setReleaseYear(seriesDto.getReleaseYear());
//        existingSeries.setImageUrl(seriesDto.getImageUrl());
//        existingSeries.setBackgroundImageUrl(seriesDto.getBackgroundImageUrl());
//        existingSeries.setDescription(seriesDto.getDescription());
//        existingSeries.setStaff(seriesDto.getStaff());
//        existingSeries.setGenre(genreRepository.findByGenreTypeIgnoreCase(seriesDto.getGenre()));
//        existingSeries.setPromoted(seriesDto.isPromoted());
//        existingSeries.setAgeLimit(seriesDto.getAgeLimit());
//        existingSeries.setImdbRating(seriesDto.getImdbRating());
//        seriesRepository.save(existingSeries);
//    }

//    public List<SeriesDto> getSeriesPromoBox(Long userId) {
//        SeriesPromoBox promoBox = seriesPromoBoxRepository.findTopByOrderByIdDesc();
//        if (promoBox == null) {
//            return Collections.emptyList();
//        }
//
//        String[] imdbIds = promoBox.getImdbIds().split(",");
//        return Arrays.stream(imdbIds)
//                .flatMap(imdbId -> getSeriesByImdbId(imdbId).stream())
//                .peek(serie -> {
//                    serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId()));
//                    serie.setUserRating(userRatingService.getCurrentUserRatingForSeries(serie.getImdbId(), userId).orElse(null));
//                })
//                .collect(Collectors.toList());
//    }
//
//    public String getSeriesPromoBoxTitle() {
//        SeriesPromoBox promoBox = seriesPromoBoxRepository.findTopByOrderByIdDesc();
//        if (promoBox != null) {
//            return promoBox.getSeriesPromoBoxTitle();
//        } else {
//            return "Trending series";
//        }
//    }
//
//    public void updateSeriesPromoBox(String title, String imdbId1, String imdbId2, String imdbId3, String imdbId4, String imdbId5) {
//        List<String> allImdbIds = Arrays.asList(imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
//        List<String> validImdbIds = new ArrayList<>();
//
//        for (String imdbId : allImdbIds) {
//            if (seriesExists(imdbId)) {
//                validImdbIds.add(imdbId);
//            } else {
//                throw new SeriesNotFoundException("Series not found");
//            }
//        }
//
//        String joinedImdbIds = String.join(",", validImdbIds);
//
//        SeriesPromoBox seriesPromoBox = new SeriesPromoBox();
//        seriesPromoBox.setSeriesPromoBoxTitle(title);
//        seriesPromoBox.setImdbIds(joinedImdbIds);
//        seriesPromoBoxRepository.save(seriesPromoBox);
//
//    }

//    private boolean seriesExists(String imdbId) {
//        return seriesRepository.existsByImdbId(imdbId);
//    }
}
