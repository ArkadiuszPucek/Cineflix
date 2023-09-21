package pl.puccini.viaplay;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.movie.Movie;
import pl.puccini.viaplay.domain.movie.MovieRepository;
import pl.puccini.viaplay.domain.series.Series;
import pl.puccini.viaplay.domain.series.SeriesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class HomeService {
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;

    public HomeService(MovieRepository movieRepository, SeriesRepository seriesRepository) {
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
    }


    public Object findRandomPromotedItem() {
        List<Object> promotedItems = new ArrayList<>();

        List<Movie> promotedMovies = movieRepository.findAllByPromotedIsTrue();
        List<Series> promotedSerials = seriesRepository.findAllByPromotedIsTrue();

        promotedItems.addAll(promotedMovies);
        promotedItems.addAll(promotedSerials);

        int size = promotedItems.size();

        if (size > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(size);
            return promotedItems.get(randomIndex);
        }else {
            return movieRepository.findById(1L);
        }
    }
}
