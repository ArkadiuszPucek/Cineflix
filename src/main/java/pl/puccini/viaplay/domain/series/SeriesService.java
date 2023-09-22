package pl.puccini.viaplay.domain.series;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeriesService {

    private final SeriesRepository seriesRepository;

    public SeriesService(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }


    public List<SeriesDto> findAllPromotedMovies() {
        return seriesRepository.findAllByPromotedIsTrue().stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }
}
