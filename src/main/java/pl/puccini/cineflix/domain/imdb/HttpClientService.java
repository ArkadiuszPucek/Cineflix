package pl.puccini.cineflix.domain.imdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class HttpClientService {
    private final HttpClient httpClient;

    public HttpClientService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String createApiRequest(String url, String apiKey, String apiHost) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", apiHost)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public JsonNode fetchSeasonsFromApi(String url, String apiKey, String apiHost) throws IOException, InterruptedException {
        HttpRequest getSeasonsIMDbApiRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", apiHost)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(getSeasonsIMDbApiRequest, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readTree(response.body());
    }
}
