package pl.puccini.viaplay.domain.imdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreRepository;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class IMDbApiService {

    private final GenreRepository genreRepository;
    private static final String RAPID_API_KEY = "a036483fe6mshaed22ea9f1c82e1p101240jsn943f47197fbe";
    private static final String RAPID_API_HOST = "mdblist.p.rapidapi.com";
    private static final String RAPID_API_MDA_HOST = "movie-database-alternative.p.rapidapi.com";

    public IMDbApiService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public MovieDto fetchIMDbData(String imdbId) throws IOException, InterruptedException {
        String apiUrl = "https://mdblist.p.rapidapi.com/?i=" + imdbId;
        String apiMdaUrl = "https://movie-database-alternative.p.rapidapi.com/?r=json&i=" + imdbId;


        HttpRequest mdblistApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest mdaApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiMdaUrl))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
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
            title = mdaApiRootNode.path("Title").asText(); // Zwróć uwagę na wielkość liter w JSON
        }

        if (year == -1) {
            year = mdaApiRootNode.path("Year").asInt(); // Zwróć uwagę na wielkość liter w JSON
        }

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = mdaApiRootNode.path("Poster").asText(); // Zwróć uwagę na wielkość liter w JSON
        }

        if (timeLine == -1) {
            timeLine = mdaApiRootNode.path("Runtime").asInt(); // Zwróć uwagę na wielkość liter w JSON
        }
        if (description == null || description.isEmpty()) {
            description = mdaApiRootNode.path("Plot").asText(); // Zwróć uwagę na wielkość liter w JSON
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
}
