package pl.puccini.viaplay.domain.imdb;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreRepository;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.service.EpisodeService;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.*;


@Service
public class IMDbApiService {

    private final GenreRepository genreRepository;
    private final EpisodeService episodeService;
    private static final String RAPID_API_KEY = "a036483fe6mshaed22ea9f1c82e1p101240jsn943f47197fbe";
    private static final String RAPID_API_HOST = "mdblist.p.rapidapi.com";
    private static final String RAPID_API_MDA_HOST = "movie-database-alternative.p.rapidapi.com";
    private static final String RAPID_API_IMDb_HOST = "imdb8.p.rapidapi.com";
    public IMDbApiService(GenreRepository genreRepository, EpisodeService episodeService) {
        this.genreRepository = genreRepository;
        this.episodeService = episodeService;
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


    public SeriesDto fetchIMDbDataForSeries(String imdbId) throws IOException, InterruptedException {

        String mdbListUrl = "https://mdblist.p.rapidapi.com/?i=" + imdbId;
        String overDetailsIMDbURL = "https://imdb8.p.rapidapi.com/title/get-overview-details?tconst=" + imdbId + "&currentCountry=US";
        String getSeasonsIMDbURL = "https://imdb8.p.rapidapi.com/title/get-seasons?tconst=" + imdbId;
        String autoCompleteIMDbURL = "https://imdb8.p.rapidapi.com/auto-complete?q=" + imdbId;


        HttpRequest mdblistApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(mdbListUrl))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest getOverDetailsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(overDetailsIMDbURL))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest getSeasonsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(getSeasonsIMDbURL))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpRequest getAutoCompleteIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(autoCompleteIMDbURL))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
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

//    private void addSeasonsAndEpisodesToSeries(Series series, String imdbId) throws IOException, InterruptedException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        HttpClient client = HttpClient.newHttpClient();
//        String getSeasonsIMDbURL = "https://imdb8.p.rapidapi.com/title/get-seasons?tconst=" + imdbId;
//        HttpRequest getSeasonsIMDbApiRequest = HttpRequest.newBuilder()
//                .uri(URI.create(getSeasonsIMDbURL))
//                .header("X-RapidAPI-Key", RAPID_API_KEY)
//                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
//                .method("GET", HttpRequest.BodyPublishers.noBody())
//                .build();
//
//        HttpResponse<String> seasonsIMDbApiResponse = client.send(getSeasonsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());
//        JsonNode seasonsIMDbApiRootNode = objectMapper.readTree(seasonsIMDbApiResponse.body());
//        for (JsonNode seasonNode : seasonsIMDbApiRootNode) {
//            int seasonNumber = seasonNode.path("season").asInt();
//            JsonNode episodesNode = seasonNode.path("episodes");
//
//            if (episodesNode.isArray()) {
//                for (JsonNode episodeNode : episodesNode) {
//                    // Informacje o epizodzie
//                    int episodeNumber = episodeNode.path("episode").asInt();
//                    String episodeTitle = episodeNode.path("title").asText();
//                    String episodeImdbId = extractImdbId(episodeNode.path("id").asText());
//
//                    // Ładowanie dodatkowych informacji o epizodzie używając episodeImdbId
//                    EpisodeDto episodeDto = loadEpisodeData(episodeImdbId);
//                    episodeDto.setEpisodeNumber(episodeNumber);
//                    episodeDto.setEpisodeTitle(episodeTitle);
//
//                    // Dodawanie epizodu do sezonu
//                    episodeService.addEpisode(episodeDto, imdbId, seasonNumber);
//                }
//            }
//        }
//    }
//
//
//    private String extractImdbId(String id) {
//        String[] parts = id.split("/");
//        return parts[parts.length - 1];
//    }
//
//    private EpisodeDto loadEpisodeData(String episodeImdbId) throws IOException, InterruptedException {
//        HttpClient client = HttpClient.newHttpClient();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        String getDetailsImdbURL = "https://imdb8.p.rapidapi.com/title/get-details?tconst=" + episodeImdbId;
//        HttpRequest getDetailsIMDbApiRequest = HttpRequest.newBuilder()
//                .uri(URI.create(getDetailsImdbURL))
//                .header("X-RapidAPI-Key", RAPID_API_KEY)
//                .header("X-RapidAPI-Host", RAPID_API_IMDb_HOST)
//                .method("GET", HttpRequest.BodyPublishers.noBody())
//                .build();
//        HttpResponse<String> getDetailsIMDbApiResponse = client.send(getDetailsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());
//        JsonNode detailsIMDbApiRootNode = objectMapper.readTree(getDetailsIMDbApiResponse.body());
//
//        EpisodeDto episodeDto = new EpisodeDto();
//
//        int runningTimeInMinutes = detailsIMDbApiRootNode.path("runningTimeInMinutes").asInt(43);
//        episodeDto.setDurationMinutes(runningTimeInMinutes);
//
//        String imageUrl = detailsIMDbApiRootNode.path("image").path("url").asText("https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Solid_white_bordered.svg/600px-Solid_white_bordered.svg.png");
//        episodeDto.setMediaUrl(imageUrl);
//
//        return episodeDto;
//
//    }
}
