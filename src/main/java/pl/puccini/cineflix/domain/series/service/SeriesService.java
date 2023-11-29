package pl.puccini.cineflix.domain.series.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.puccini.cineflix.domain.series.repository.SeasonRepository;
import pl.puccini.cineflix.domain.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.user.service.UserListService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeriesService {

    private static final String RAPID_API_KEY = "a036483fe6mshaed22ea9f1c82e1p101240jsn943f47197fbe";
    private static final String RAPID_API_IMDb_HOST = "imdb8.p.rapidapi.com";

    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final IMDbApiService imdbApiService;
    private final GenreRepository genreRepository;
    private final UserListService userListService;
    private final GenreService genreService;
    private final EpisodeService episodeService;

    public SeriesService(SeriesRepository seriesRepository, SeasonRepository seasonRepository, IMDbApiService imdbApiService, GenreRepository genreRepository, UserListService userListService, GenreService genreService, EpisodeService episodeService) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.imdbApiService = imdbApiService;
        this.genreRepository = genreRepository;
        this.userListService = userListService;
        this.genreService = genreService;
        this.episodeService = episodeService;
    }


    public List<SeriesDto> findAllPromotedSeries() {
//        List<ViewingHistory> viewed = viewingHistoryRepository.findByUserId(userId);
//        Set<String> viewedImdbIds = viewed.stream()
//                .map(history -> history.getEpisode().getSeries().getImdbId())
//                .collect(Collectors.toSet());
//
//        // Pobierz listę proponowanych seriali, wykluczając obejrzane
//        List<SeriesDto> recommended = // metoda pobierająca proponowane seriale
//        return recommended.stream()
//                .filter(series -> !viewedImdbIds.contains(series.getImdbId()))
//                .collect(Collectors.toList());

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
        seriesDtos.forEach(serie -> serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId())));
        return seriesDtos;
    }

    public SeriesDto findSeriesByTitle(String title, Long userId) {
        Series series = seriesRepository.findByTitleIgnoreCase(title);
        if (series == null) {
            return null;
        }
        SeriesDto mappedSeries = SeriesDtoMapper.map(series);
        mappedSeries.setOnUserList(userListService.isOnList(userId, series.getImdbId()));
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
        // Pobierz listę sezonów na podstawie imdbId
        List<Season> seasons = seasonRepository.findSeasonsBySeriesImdbId(imdbId);

        // Mapuj sezon na SeasonDto za pomocą SeasonDtoMapper

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

        addSeasonsAndEpisodesToSeries(series, seriesApiDto.getImdbId());
        return series;
    }

    private void addSeasonsAndEpisodesToSeries(Series series, String imdbId) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient client = HttpClient.newHttpClient();
        String getSeasonsIMDbURL = "https://imdb8.p.rapidapi.com/title/get-seasons?tconst=" + imdbId;

        HttpRequest getSeasonsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(getSeasonsIMDbURL))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();


        HttpResponse<String> seasonsIMDbApiResponse = client.send(getSeasonsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());

        JsonNode seasonsIMDbApiRootNode = objectMapper.readTree(seasonsIMDbApiResponse.body());


        for (JsonNode seasonNode : seasonsIMDbApiRootNode) {
            int seasonNumber = seasonNode.path("season").asInt();
            JsonNode episodesNode = seasonNode.path("episodes");

            if (episodesNode.isArray()) {
                for (JsonNode episodeNode : episodesNode) {
                    // Informacje o epizodzie
                    int episodeNumber = episodeNode.path("episode").asInt();
                    String episodeTitle = episodeNode.path("title").asText();
                    String episodeImdbId = extractImdbId(episodeNode.path("id").asText());

                    // Ładowanie dodatkowych informacji o epizodzie używając episodeImdbId
                    EpisodeDto episodeDto = loadEpisodeData(episodeImdbId);
                    episodeDto.setEpisodeNumber(episodeNumber);
                    episodeDto.setEpisodeTitle(episodeTitle);

                    // Dodawanie epizodu do sezonu
                    episodeService.addEpisode(episodeDto, imdbId, seasonNumber);
                }
            }
        }
    }

    private String extractImdbId(String id) {
        String[] parts = id.split("/");
        return parts[parts.length - 1];
    }

    private EpisodeDto loadEpisodeData(String episodeImdbId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        String getDetailsImdbURL = "https://imdb8.p.rapidapi.com/title/get-details?tconst=" + episodeImdbId;
        HttpRequest getDetailsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(getDetailsImdbURL))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> getDetailsIMDbApiResponse = client.send(getDetailsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode detailsIMDbApiRootNode = objectMapper.readTree(getDetailsIMDbApiResponse.body());


        String getOverDetailsIMDbURL = "https://imdb8.p.rapidapi.com/title/get-overview-details?tconst=" + episodeImdbId + "&currentCountry=US";
        HttpRequest getOverDetailsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(getOverDetailsIMDbURL))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> getOverDetailsIMDbApiResponse = client.send(getOverDetailsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode overDetailsIMDbApiRootNode = objectMapper.readTree(getOverDetailsIMDbApiResponse.body());

        EpisodeDto episodeDto = new EpisodeDto();

        int runningTimeInMinutes = detailsIMDbApiRootNode.path("runningTimeInMinutes").asInt(43);
        episodeDto.setDurationMinutes(runningTimeInMinutes);

        String imageUrl = detailsIMDbApiRootNode.path("image").path("url").asText("https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Solid_white_bordered.svg/600px-Solid_white_bordered.svg.png");
        episodeDto.setImageUrl(imageUrl);

        String plotOutline = overDetailsIMDbApiRootNode.path("plotOutline").path("text").asText();
        episodeDto.setEpisodeDescription(plotOutline);
        episodeDto.setMediaUrl("https://www.youtube.com/watch?v=hQqBsvIB40E&ab_channel=jurak");

        return episodeDto;

    }


    public List<SeriesDto> findAllMoviesInService() {
        return seriesRepository.findAll().stream()
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


}
