package pl.puccini.cineflix.domain.imdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreRepository;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.web.admin.ConfigLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.*;


@Service
public class IMDbApiService {

    private final GenreRepository genreRepository;
    private static final String RAPID_API_HOST = "mdblist.p.rapidapi.com";
    private static final String RAPID_API_MDA_HOST = "movie-database-alternative.p.rapidapi.com";
    private static final String RAPID_API_IMDb_HOST = "imdb8.p.rapidapi.com";
    private String rapidApiKey;

    public IMDbApiService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
        loadRapidApiKey();
    }
    private void loadRapidApiKey() {
        ConfigLoader config = new ConfigLoader();
        rapidApiKey = config.getProperty("rapid_api_key");
    }

    public MovieDto fetchIMDbData(String imdbId) throws IOException, InterruptedException {
        String apiUrl = "https://mdblist.p.rapidapi.com/?i=" + imdbId;
        String apiMdaUrl = "https://movie-database-alternative.p.rapidapi.com/?r=json&i=" + imdbId;


        HttpRequest mdblistApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", RAPID_API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest mdaApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiMdaUrl))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", RAPID_API_MDA_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> mdblistApiResponse = client.send(mdblistApiRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> mdaApiResponse = client.send(mdaApiRequest, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode mdblistApiRootNode = objectMapper.readTree(mdblistApiResponse.body());
        JsonNode mdaApiRootNode = objectMapper.readTree(mdaApiResponse.body());

        MovieDto movieDto = new MovieDto();

        String title = mdblistApiRootNode.path("title").asText(null);
        int year = mdblistApiRootNode.path("year").asInt(-1);
        String imageUrl = mdblistApiRootNode.path("poster").asText(null);
        String backgroundImageUrl = mdblistApiRootNode.path("backdrop").asText("https://creativity103.com/collections/Surreal/blurredcolours3780.jpg");
        String mediaUrl = mdblistApiRootNode.path("trailer").asText("https://www.youtube.com/watch?v=hQqBsvIB40E&ab_channel=jurak");
        int timeLine = mdblistApiRootNode.path("runtime").asInt(-1);
        int ageLimit = mdblistApiRootNode.path("age_rating").asInt(16);
        String description = mdblistApiRootNode.path("description").asText(null);
        String actors = mdaApiRootNode.path("Actors").asText("brak informacji");
        String directedBy = mdaApiRootNode.path("Director").asText("brak informacji");
        String language = mdaApiRootNode.path("Language").asText("Polski");
        String genres = mdaApiRootNode.path("Genre").asText("Crime");
        List<String> genreList = Arrays.asList(genres.split(", "));
        Optional<Genre> matchedGenre = genreList.stream()
                .map(genreRepository::findByGenreTypeIgnoreCase)
                .filter(Objects::nonNull)
                .findFirst();
        double imdbRating = mdaApiRootNode.path("imdbRating").asInt(-1);


        if (title == null || title.isEmpty()) {
            title = mdaApiRootNode.path("Title").asText();
        }

        if (year == -1) {
            year = mdaApiRootNode.path("Year").asInt();
        }

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = mdaApiRootNode.path("Poster").asText();
        }

        if (timeLine == -1) {
            timeLine = mdaApiRootNode.path("Runtime").asInt();
        }
        if (description == null || description.isEmpty()) {
            description = mdaApiRootNode.path("Plot").asText();
        }
        if (imdbRating == -1) {
            JsonNode ratingsArray = mdblistApiRootNode.path("ratings");
            if (ratingsArray.isArray()) {
                for (JsonNode ratingElement : ratingsArray) {
                    if ("imdb".equals(ratingElement.path("source").asText())) {
                        movieDto.setImdbRating(ratingElement.path("value").asDouble());
                        break;
                    }
                }
            }
        }

        movieDto.setImdbId(imdbId);
        movieDto.setTitle(title);
        movieDto.setReleaseYear(year);
        movieDto.setImageUrl(imageUrl);
        movieDto.setBackgroundImageUrl(backgroundImageUrl);
        movieDto.setMediaUrl(mediaUrl);
        movieDto.setTimeline(timeLine);
        movieDto.setAgeLimit(ageLimit);
        movieDto.setDescription(description);
        movieDto.setStaff(actors);
        movieDto.setDirectedBy(directedBy);
        movieDto.setLanguages(language);
        matchedGenre.ifPresent(genre -> movieDto.setGenre(genre.getGenreType()));
        movieDto.setImdbRating(imdbRating);
        movieDto.setImdbUrl("https://www.imdb.com/title/" + imdbId);

        return movieDto;
    }


    public SeriesDto fetchIMDbDataForSeries(String imdbId) throws IOException, InterruptedException {

        String mdbListUrl = "https://mdblist.p.rapidapi.com/?i=" + imdbId;
        String overDetailsIMDbURL = "https://imdb8.p.rapidapi.com/title/get-overview-details?tconst=" + imdbId + "&currentCountry=US";
        String getSeasonsIMDbURL = "https://imdb8.p.rapidapi.com/title/get-seasons?tconst=" + imdbId;
        String autoCompleteIMDbURL = "https://imdb8.p.rapidapi.com/auto-complete?q=" + imdbId;


        HttpRequest mdblistApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(mdbListUrl))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", RAPID_API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest getOverDetailsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(overDetailsIMDbURL))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest getSeasonsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(getSeasonsIMDbURL))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest getAutoCompleteIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(autoCompleteIMDbURL))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();




        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> mdblistApiResponse = client.send(mdblistApiRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> overDetailsIMDbApiResponse = client.send(getOverDetailsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> seasonsIMDbApiResponse = client.send(getSeasonsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> autoCompleteIMDbApiResponse = client.send(getAutoCompleteIMDbApiRequest, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode mdblistApiRootNode = objectMapper.readTree(mdblistApiResponse.body());
        JsonNode overDetailsIMDbApiRootNode = objectMapper.readTree(overDetailsIMDbApiResponse.body());
        JsonNode seasonsIMDbApiRootNode = objectMapper.readTree(seasonsIMDbApiResponse.body());
        JsonNode autoCompleteIMDbApiRootNode = objectMapper.readTree(autoCompleteIMDbApiResponse.body());

        SeriesDto seriesDto = new SeriesDto();

        String title = mdblistApiRootNode.path("title").asText(null);
        int releaseYear = mdblistApiRootNode.path("year").asInt(-1);
        String imageUrl = mdblistApiRootNode.path("poster").asText(null);
        String backgroundImageUrl = mdblistApiRootNode.path("backdrop").asText("https://creativity103.com/collections/Surreal/blurredcolours3780.jpg");
        String description = mdblistApiRootNode.path("description").asText(null);
        String staff = autoCompleteIMDbApiRootNode.path("d").get(0).path("s").asText(" ");
        JsonNode genresNode = overDetailsIMDbApiRootNode.path("genres");
        int ageLimit = mdblistApiRootNode.path("age_rating").asInt(16);
        JsonNode ratingsNode = overDetailsIMDbApiRootNode.path("ratings");


        if (!ratingsNode.isMissingNode()) {
            double rating = ratingsNode.path("rating").asDouble(5.0);
            seriesDto.setImdbRating(rating);
        }

        List<String> genresList = new ArrayList<>();
        if (genresNode.isArray()) {
            genresNode.forEach(genre -> genresList.add(genre.asText()));
        }
        Optional<Genre> matchedGenre = genresList.stream()
                .map(genreRepository::findByGenreTypeIgnoreCase)
                .filter(Objects::nonNull)
                .findFirst();
        if (matchedGenre.isPresent()) {
            seriesDto.setGenre(matchedGenre.get().getGenreType());
        } else {
            seriesDto.setGenre("Nieznany gatunek");
        }

        seriesDto.setImdbId(imdbId);
        seriesDto.setTitle(title);
        seriesDto.setReleaseYear(releaseYear);
        seriesDto.setImageUrl(imageUrl);
        seriesDto.setBackgroundImageUrl(backgroundImageUrl);
        seriesDto.setAgeLimit(ageLimit);
        seriesDto.setDescription(description);
        seriesDto.setStaff(staff);
        matchedGenre.ifPresent(genre -> seriesDto.setGenre(genre.getGenreType()));
        seriesDto.setImdbUrl("https://www.imdb.com/title/" + imdbId);
        seriesDto.setSeasonsCount(seasonsIMDbApiRootNode.size());

        return seriesDto;
    }
}
