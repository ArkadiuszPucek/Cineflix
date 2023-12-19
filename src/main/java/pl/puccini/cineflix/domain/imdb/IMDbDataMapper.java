package pl.puccini.cineflix.domain.imdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.exceptions.DataMappingException;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.genre.repository.GenreRepository;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

import java.util.*;

@Service
public class IMDbDataMapper {

    private final ObjectMapper objectMapper;
    private final GenreRepository genreRepository;

    public IMDbDataMapper(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
        this.objectMapper = new ObjectMapper();
    }

    public MovieDto mapToMovieDto(String mdbListApiResponse, String mdaApiResponse) {
        try {
            JsonNode mdblistApiRootNode = objectMapper.readTree(mdbListApiResponse);
            JsonNode mdaApiRootNode = objectMapper.readTree(mdaApiResponse);

            return parseMovieDto(mdblistApiRootNode, mdaApiRootNode);
        } catch (JsonProcessingException e) {
            throw new DataMappingException("Error processing JSON for movie mapping", e);
        }
    }

    public SeriesDto mapToSeriesDto(String mdbListApi, String overDetails, String getSeasons, String autoComplete) {
        try {
            JsonNode mdbListApiRootNode = objectMapper.readTree(mdbListApi);
            JsonNode overDetailsRootNode = objectMapper.readTree(overDetails);
            JsonNode getSeasonsRootNode = objectMapper.readTree(getSeasons);
            JsonNode autoCompleteRootNode = objectMapper.readTree(autoComplete);

            return parseSeriesDto(mdbListApiRootNode, overDetailsRootNode, getSeasonsRootNode, autoCompleteRootNode);
        } catch (JsonProcessingException e) {
            throw new DataMappingException("Error processing JSON for series mapping", e);
        }
    }

    public EpisodeDto mapToEpisodeDto(String getDetailsApi, String overDetailsApi) {
        try {
            JsonNode getDetailsRootNode = objectMapper.readTree(getDetailsApi);
            JsonNode overDetailsRootNode = objectMapper.readTree(overDetailsApi);

            return parseEpisodeDto(getDetailsRootNode, overDetailsRootNode);
        } catch (JsonProcessingException e) {
            throw new DataMappingException("Error processing JSON for episode mapping", e);
        }
    }

    private MovieDto parseMovieDto(JsonNode mdblistApiRootNode, JsonNode mdaApiRootNode) {
        MovieDto movieDto = new MovieDto();

        String imdbId = mdblistApiRootNode.path("imdbid").asText(null);
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

        if (imdbId == null || imdbId.isEmpty()) {
            imdbId = mdaApiRootNode.path("Imdbid").asText();
        }

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
        matchedGenre.ifPresent(genre -> {
            if ("Animation".equalsIgnoreCase((genre.getGenreType()))){
                movieDto.setGenre("Kids");
            }else {
                movieDto.setGenre(genre.getGenreType());
            }
        });
        movieDto.setImdbRating(imdbRating);
        movieDto.setImdbUrl("https://www.imdb.com/title/" + imdbId);

        return movieDto;
    }

    private SeriesDto parseSeriesDto(JsonNode mdbListApiRootNode, JsonNode overDetailsRootNode,
                                     JsonNode getSeasonsRootNode, JsonNode autoCompleteRootNode) {
        SeriesDto seriesDto = new SeriesDto();

        String imdbId = mdbListApiRootNode.path("imdbid").asText(null);
        String title = mdbListApiRootNode.path("title").asText(null);
        int releaseYear = mdbListApiRootNode.path("year").asInt(-1);
        String imageUrl = mdbListApiRootNode.path("poster").asText(null);
        String backgroundImageUrl = mdbListApiRootNode.path("backdrop").asText("https://creativity103.com/collections/Surreal/blurredcolours3780.jpg");
        String description = mdbListApiRootNode.path("description").asText(null);
        String staff = autoCompleteRootNode.path("d").get(0).path("s").asText(" ");
        JsonNode genresNode = overDetailsRootNode.path("genres");
        int ageLimit = mdbListApiRootNode.path("age_rating").asInt(16);
        JsonNode ratingsNode = overDetailsRootNode.path("ratings");


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
            seriesDto.setGenre("Unknown genre");
        }

        seriesDto.setImdbId(imdbId);
        seriesDto.setTitle(title);
        seriesDto.setReleaseYear(releaseYear);
        seriesDto.setImageUrl(imageUrl);
        seriesDto.setBackgroundImageUrl(backgroundImageUrl);
        seriesDto.setAgeLimit(ageLimit);
        seriesDto.setDescription(description);
        seriesDto.setStaff(staff);
        matchedGenre.ifPresent(genre -> {
            if ("Animation".equalsIgnoreCase((genre.getGenreType()))){
                seriesDto.setGenre("Kids");
            }else {
                seriesDto.setGenre(genre.getGenreType());
            }
        });
        seriesDto.setImdbUrl("https://www.imdb.com/title/" + imdbId);
        seriesDto.setSeasonsCount(getSeasonsRootNode.size());

        return seriesDto;
    }

    private EpisodeDto parseEpisodeDto(JsonNode getDetailsRootNode, JsonNode overDetailsRootNode) {
        EpisodeDto episodeDto = new EpisodeDto();

        int runningTimeInMinutes = getDetailsRootNode.path("runningTimeInMinutes").asInt(43);
        episodeDto.setDurationMinutes(runningTimeInMinutes);

        String imageUrl = getDetailsRootNode.path("image").path("url").asText("https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Solid_white_bordered.svg/600px-Solid_white_bordered.svg.png");
        episodeDto.setImageUrl(imageUrl);

        String plotOutline = overDetailsRootNode.path("plotOutline").path("text").asText();
        episodeDto.setEpisodeDescription(plotOutline);
        episodeDto.setMediaUrl("https://www.youtube.com/watch?v=hQqBsvIB40E&ab_channel=jurak");

        return episodeDto;
    }
}
