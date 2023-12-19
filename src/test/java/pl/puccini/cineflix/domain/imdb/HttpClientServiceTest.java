package pl.puccini.cineflix.domain.imdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpClientServiceTest {

    @Mock
    private HttpClientWrapper httpClientWrapper;

    @InjectMocks
    private HttpClientService httpClientService;

    @Test
    void createApiRequestTest() throws IOException, InterruptedException {
        String expectedResponse = "test response";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(expectedResponse);

        when(httpClientWrapper.send(any(HttpRequest.class))).thenReturn(mockResponse);

        String response = httpClientService.createApiRequest("http://testurl.com", "testKey", "testHost");

        assertEquals(expectedResponse, response);
        verify(httpClientWrapper).send(any(HttpRequest.class));
    }
}