package pl.puccini.viaplay.domain.imdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;


@Service
public class IMDbApiService {
    private static final String RAPID_API_KEY = "a036483fe6mshaed22ea9f1c82e1p101240jsn943f47197fbe";
    private static final String RAPID_API_HOST = "imdb8.p.rapidapi.com";

    public IMDbData fetchIMDbData (String imdbId) throws IOException, InterruptedException {
        String apiUrl = "https://imdb8.p.rapidapi.com/title/get-overview-details?tconst=" + imdbId + "&currentCountry=US";

//        TODO - zmień api na https://rapidapi.com/linaspurinis/api/mdblist,

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.body());

        IMDbData imdbData = new IMDbData();

        JsonNode titleNode = rootNode.path("title");

        imdbData.setTitle(titleNode.path("title").asText());
        imdbData.setReleaseYear(titleNode.path("year").asInt());
        imdbData.setImageUrl(titleNode.path("image").path("url").asText());
        imdbData.setTimeline(titleNode.path("runningTimeInMinutes").asInt());

        JsonNode ratingsNode = rootNode.path("ratings");

        imdbData.setImdbRating(ratingsNode.path("rating").asDouble());
        imdbData.setImdbUrl("https://www.imdb.com/title/" + imdbId);

        JsonNode plotOutlineNode = rootNode.path("plotOutline");
        imdbData.setDescription(plotOutlineNode.path("text").asText());


//        private String imdbId;
//        private String title;
//        private Integer releaseYear;
//        private String imageUrl;
//        private String backgroundImageUrl; // Jeśli jest to inne niż 'imageUrl', musisz to doprecyzować
//        private String mediaUrl; // Może to być URL do filmu, jeśli jest dostępny
//        private Integer timeline;
//        private Integer ageLimit; // Możesz to wywnioskować z obiektu 'certificates' lub zdefiniować samemu
//        private String description;
//        private String staff; // Nie widać w JSON, musisz określić jak to zapisywać
//        private String directedBy; // Nie widać w JSON, musisz określić jak to zapisywać
//        private String languages; // Nie widać w JSON, musisz określić jak to zapisywać
//        private String genre; // Z listy 'genres'
//        private boolean promoted;
//        private double imdbRating;
//        private String imdbUrl;

//        IMDbApiResponse apiResponse = objectMapper.readValue(response.body(), IMDbApiResponse.class);
//        double imdbRating = apiResponse.getImdbRating().getImdbRating();


        return imdbData;
    }
}
