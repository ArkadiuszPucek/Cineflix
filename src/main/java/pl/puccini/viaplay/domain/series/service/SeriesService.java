package pl.puccini.viaplay.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDtoMapper;

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
