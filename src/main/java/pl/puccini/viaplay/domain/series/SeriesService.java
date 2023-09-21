package pl.puccini.viaplay.domain.series;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.movie.MovieDtoMapper;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.series.dto.SeriesDto;

import java.util.List;

@Service
public class SeriesService {

    private SeriesRepository seriesRepository;

    public SeriesService(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public List<SeriesDto> findAllPromotedMovies() {
        return seriesRepository.findAllByPromotedIsTrue().stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }
}
