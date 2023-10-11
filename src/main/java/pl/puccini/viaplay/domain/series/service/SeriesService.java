package pl.puccini.viaplay.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDtoMapper;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;

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

    public List<SeriesDto> getSeriesByImdbId(String imdbId){
        return seriesRepository.findAllByImdbId(imdbId).stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public List<SeriesDto> getSeriesByGenre(Genre genre){
        return seriesRepository.findAllByGenre(genre).stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public SeriesDto findByTitle(String title) {
        Series series = seriesRepository.findByTitleIgnoreCase(title);
        if (series == null){
            return null;
        }
        return SeriesDtoMapper.map(series);
    }
}
