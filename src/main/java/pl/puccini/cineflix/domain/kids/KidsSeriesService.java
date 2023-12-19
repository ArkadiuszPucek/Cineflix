package pl.puccini.cineflix.domain.kids;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

import java.util.List;

@Service
public class KidsSeriesService {
    private final SeriesFacade seriesFacade;
    private static final String KIDS_GENRE = "Kids";

    public KidsSeriesService(SeriesFacade seriesFacade) {
        this.seriesFacade = seriesFacade;
    }

    public List<SeriesDto> getAllKidsSeries(Long userId) {
        return seriesFacade.getSeriesByGenre(KIDS_GENRE, userId);
    }
}
