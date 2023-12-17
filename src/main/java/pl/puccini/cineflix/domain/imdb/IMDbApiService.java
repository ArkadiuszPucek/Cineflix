package pl.puccini.cineflix.domain.imdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFacade;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.config.configLoader.ConfigLoader;

import java.io.IOException;


@Service
public class IMDbApiService {
    private final HttpClientService httpClientService;
    private final IMDbApiUrlBuilder imDbApiUrlBuilder;
    private final IMDbDataMapper imDbDataMapper;
    private final ObjectMapper objectMapper;
    private final EpisodeFacade episodeFacade;
    private static final String RAPID_API_HOST = "mdblist.p.rapidapi.com";
    private static final String RAPID_API_MDA_HOST = "movie-database-alternative.p.rapidapi.com";
    private static final String RAPID_API_IMDb_HOST = "imdb8.p.rapidapi.com";
    private String rapidApiKey;

    public IMDbApiService(HttpClientService httpClientService, IMDbApiUrlBuilder imDbApiUrlBuilder, IMDbDataMapper imDbDataMapper, ObjectMapper objectMapper, EpisodeFacade episodeFacade) {
        this.httpClientService = httpClientService;
        this.imDbApiUrlBuilder = imDbApiUrlBuilder;
        this.imDbDataMapper = imDbDataMapper;
        this.objectMapper = objectMapper;
        this.episodeFacade = episodeFacade;
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
        String mdbListApiRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildMovieDataBaseListAPIUrl(imdbId), rapidApiKey, RAPID_API_HOST);
        String mdaApiRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildMovieDatabaseAlternativeAPIUrl(imdbId), rapidApiKey, RAPID_API_MDA_HOST);

        return imDbDataMapper.mapToMovieDto(mdbListApiRootNode, mdaApiRootNode);
    }

    public SeriesDto fetchIMDbDataForSeries(String imdbId) throws IOException, InterruptedException{
        String mdbListApiRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildMovieDataBaseListAPIUrl(imdbId), rapidApiKey, RAPID_API_HOST);
        String overDetailsRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildOverDetailsIMDbAPIUrl(imdbId), rapidApiKey, RAPID_API_IMDb_HOST);
        String getSeasonsRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildGetSeasonsIMDbAPIUrl(imdbId), rapidApiKey, RAPID_API_IMDb_HOST);
        String autoCompleteRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildAutoCompleteIMDbAPIUrl(imdbId), rapidApiKey, RAPID_API_IMDb_HOST);

        return imDbDataMapper.mapToSeriesDto(mdbListApiRootNode, overDetailsRootNode, getSeasonsRootNode, autoCompleteRootNode);
    }

    public EpisodeDto fetchIMDbDataForEpisodes(String episodeImdbId) throws IOException, InterruptedException{
        String getDetailsRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildGetDetailsIMDbAPIUrl(episodeImdbId), rapidApiKey, RAPID_API_IMDb_HOST);
        String overDetailsRootNode = makeApiRequestWithRateLimitHandling(imDbApiUrlBuilder.buildOverDetailsIMDbAPIUrl(episodeImdbId), rapidApiKey, RAPID_API_IMDb_HOST);

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
                episodeFacade.addEpisode(episodeDto, seriesImdbId, seasonNumber);
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

    private String makeApiRequestWithRateLimitHandling(String url, String apiKey, String apiHost) throws IOException, InterruptedException {
        for (int i = 0; i < 2; i++) {
            String response = httpClientService.createApiRequest(url, apiKey, apiHost);
            JsonNode responseJson = objectMapper.readTree(response);
            if (!responseJson.has("message")) {
                return response;
            }
            Thread.sleep(1000);
        }
        throw new IOException("Exceeded rate limit multiple times.");
    }
}
