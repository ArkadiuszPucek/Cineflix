package pl.puccini.cineflix.domain.imdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.web.admin.ConfigLoader;

import java.io.IOException;


@Service
public class IMDbApiService {

    private final EpisodeService episodeService;
    private final HttpClientService httpClientService;
    private final IMDbApiUrlBuilder imDbApiUrlBuilder;
    private final IMDbDataMapper imDbDataMapper;
    private final ObjectMapper objectMapper;
    private static final String RAPID_API_HOST = "mdblist.p.rapidapi.com";
    private static final String RAPID_API_MDA_HOST = "movie-database-alternative.p.rapidapi.com";
    private static final String RAPID_API_IMDb_HOST = "imdb8.p.rapidapi.com";
    private String rapidApiKey;

    public IMDbApiService(@Lazy EpisodeService episodeService, HttpClientService httpClientService, IMDbApiUrlBuilder imDbApiUrlBuilder, IMDbDataMapper imDbDataMapper, ObjectMapper objectMapper) {
        this.episodeService = episodeService;
        this.httpClientService = httpClientService;
        this.imDbApiUrlBuilder = imDbApiUrlBuilder;
        this.imDbDataMapper = imDbDataMapper;
        this.objectMapper = objectMapper;
        loadRapidApiKey();
    }
    private void loadRapidApiKey() {
        ConfigLoader config = new ConfigLoader();
        rapidApiKey = config.getProperty("rapid_api_key");
    }

    public String fetchIMDbForTypeCheck(String imdbId) throws IOException, InterruptedException{
        String mdbListApiRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildMovieDataBaseListAPIUrl(imdbId), rapidApiKey, RAPID_API_HOST);
        return objectMapper.readTree(mdbListApiRootNode).path("type").asText();
    }

    public MovieDto fetchIMDbDataForMovies(String imdbId) throws IOException, InterruptedException {
        String mdbListApiRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildMovieDataBaseListAPIUrl(imdbId), rapidApiKey, RAPID_API_HOST);
        String mdaApiRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildMovieDatabaseAlternativeAPIUrl(imdbId), rapidApiKey, RAPID_API_MDA_HOST);

        return imDbDataMapper.mapToMovieDto(mdbListApiRootNode, mdaApiRootNode);
    }

    public SeriesDto fetchIMDbDataForSeries(String imdbId) throws IOException, InterruptedException{
        String mdbListApiRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildMovieDataBaseListAPIUrl(imdbId), rapidApiKey, RAPID_API_HOST);
        String overDetailsRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildOverDetailsIMDbAPIUrl(imdbId), rapidApiKey, RAPID_API_IMDb_HOST);
        String getSeasonsRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildGetSeasonsIMDbAPIUrl(imdbId), rapidApiKey, RAPID_API_IMDb_HOST);
        String autoCompleteRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildAutoCompleteIMDbAPIUrl(imdbId), rapidApiKey, RAPID_API_IMDb_HOST);

        return imDbDataMapper.mapToSeriesDto(mdbListApiRootNode, overDetailsRootNode, getSeasonsRootNode, autoCompleteRootNode);
    }

    public EpisodeDto fetchIMDbDataForEpisodes(String episodeImdbId) throws IOException, InterruptedException{
        String getDetailsRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildGetDetailsIMDbAPIUrl(episodeImdbId), rapidApiKey, RAPID_API_IMDb_HOST);
        String overDetailsRootNode = httpClientService.createApiRequest(imDbApiUrlBuilder.buildOverDetailsIMDbAPIUrl(episodeImdbId), rapidApiKey, RAPID_API_IMDb_HOST);

        return imDbDataMapper.mapToEpisodeDto(getDetailsRootNode, overDetailsRootNode);
    }

    public void fetchIMDbDataAndAddSeasonsAndEpisodesToSeries(String imdbId) throws IOException, InterruptedException{
        JsonNode seasonsNode = httpClientService.fetchSeasonsFromApi(imDbApiUrlBuilder.getSeasonsIMDbAPIUrl(imdbId), rapidApiKey, RAPID_API_IMDb_HOST);
        processSeasons(seasonsNode, imdbId);
    }

    private void processSeasons(JsonNode seasonsNode, String seriesImdbId) {
        for (JsonNode seasonNode : seasonsNode) {
            int seasonNumber = seasonNode.path("season").asInt();
            processEpisodes(seasonNode.path("episodes"), seriesImdbId, seasonNumber);
        }
    }

    private void processEpisodes(JsonNode episodesNode, String seriesImdbId, int seasonNumber) {
        if (episodesNode.isArray()) {
            for (JsonNode episodeNode : episodesNode) {
                EpisodeDto episodeDto = createEpisodeDto(episodeNode);
                episodeService.addEpisode(episodeDto, seriesImdbId, seasonNumber);
            }
        }
    }

    private EpisodeDto createEpisodeDto(JsonNode episodeNode) {
        String episodeImdbId = extractImdbId(episodeNode.path("id").asText());
        EpisodeDto episodeDto = null;
        try {
            episodeDto = fetchIMDbDataForEpisodes(episodeImdbId);
        } catch (IOException e) {
            throw new EpisodeNotFoundException("Episode not found");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        episodeDto.setEpisodeNumber(episodeNode.path("episode").asInt());
        episodeDto.setEpisodeTitle(episodeNode.path("title").asText());
        return episodeDto;
    }

    private String extractImdbId(String id) {
        String[] parts = id.split("/");
        return parts[parts.length - 1];
    }
}
