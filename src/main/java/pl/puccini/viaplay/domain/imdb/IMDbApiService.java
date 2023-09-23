package pl.puccini.viaplay.domain.imdb;

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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("X-RapidAPI-Key", RAPID_API_KEY)
                .header("X-RapidAPI-Host", RAPID_API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        IMDbApiResponse apiResponse = objectMapper.readValue(response.body(), IMDbApiResponse.class);

        double imdbRating = apiResponse.getImdbRating().getImdbRating();
        String imdbUrl = "https://www.imdb.com/title/" + imdbId;

        IMDbData imdbData = new IMDbData();
        imdbData.setImdbRating(imdbRating);
        imdbData.setImdbUrl(imdbUrl);

        return imdbData;
    }
}
