package pl.puccini.viaplay.domain.series.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreRepository;
import pl.puccini.viaplay.domain.imdb.IMDbApiService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.dto.MovieDtoMapper;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.viaplay.domain.series.dto.seasonDto.SeasonDto;
import pl.puccini.viaplay.domain.series.dto.seasonDto.SeasonDtoMapper;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDtoMapper;
import pl.puccini.viaplay.domain.series.model.Episode;
import pl.puccini.viaplay.domain.series.model.Season;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.repository.SeasonRepository;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeriesService {

    private static final String RAPID_API_KEY = "a036483fe6mshaed22ea9f1c82e1p101240jsn943f47197fbe";
    private static final String RAPID_API_IMDb_HOST = "imdb8.p.rapidapi.com";

    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final IMDbApiService imdbApiService;
    private final GenreRepository genreRepository;
    private final EpisodeService episodeService;

    public SeriesService(SeriesRepository seriesRepository, SeasonRepository seasonRepository, IMDbApiService imdbApiService, GenreRepository genreRepository, EpisodeService episodeService) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.imdbApiService = imdbApiService;
        this.genreRepository = genreRepository;
        this.episodeService = episodeService;
    }


    public List<SeriesDto> findAllPromotedMovies() {
        return seriesRepository.findAllByPromotedIsTrue().stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public List<SeriesDto> getSeriesByImdbId(String imdbId){
        return seriesRepository.findAllByImdbId(imdbId).stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public List<SeriesDto> getSeriesByGenre(Genre genre){
        return seriesRepository.findAllByGenre(genre).stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public SeriesDto findByTitle(String title) {
        Series series = seriesRepository.findByTitleIgnoreCase(title);
        if (series == null){
            return null;
        }
        return SeriesDtoMapper.map(series);
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
        List<SeasonDto> seasonDtos = seasons.stream()
                .map(SeasonDtoMapper::map)
                .collect(Collectors.toList());

        return seasonDtos;
    }

    public List<EpisodeDto> getEpisodesForSeason(Long seasonId) {
        Season season = seasonRepository.findById(seasonId).orElse(null);
        if (season == null) {
            // Obsłuż przypadek, gdy sezon nie istnieje
            return Collections.emptyList();
        }

        List<Episode> episodes = season.getEpisodes();
        List<EpisodeDto> episodeDtos = new ArrayList<>();

        for (Episode episode : episodes) {
            EpisodeDto episodeDto = EpisodeDtoMapper.map(episode);
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
        series.setImdbUrl("https://www.imdb.com/title/"+seriesDto.getImdbId());
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


    public boolean updateSeries(SeriesDto seriesDto) {
        Series existingSeries = seriesRepository.findSeriesByImdbId(seriesDto.getImdbId());

        if (existingSeries != null) {
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
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteSeriesByImdbId(String imdbId) {
        Series seriesByImdbId = seriesRepository.findSeriesByImdbId(imdbId);
        if (seriesByImdbId != null) {
            seriesRepository.delete(seriesByImdbId);
            return true;
        }
        return false;
    }
}
