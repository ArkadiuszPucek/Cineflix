package pl.puccini.cineflix.domain.imdb;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Component
public class HttpClientWrapper {
    private final HttpClient httpClient;

    public HttpClientWrapper() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
