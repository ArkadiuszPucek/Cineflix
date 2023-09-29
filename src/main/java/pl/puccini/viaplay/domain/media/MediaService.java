package pl.puccini.viaplay.domain.media;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.movie.repository.MovieRepository;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;

@Service
public class MediaService {
    private final SeriesRepository seriesRepository;
    private final MovieRepository movieRepository;

    public MediaService(SeriesRepository seriesRepository, MovieRepository movieRepository) {
        this.seriesRepository = seriesRepository;
        this.movieRepository = movieRepository;
    }


}
