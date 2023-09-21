package pl.puccini.viaplay.domain.series;

import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.series.dto.SeriesDto;

public class SeriesDtoMapper {
    public static SeriesDto map(Series series) {
        return new SeriesDto(
                series.getId(),
                series.getTitle(),
                series.getReleaseYear(),
                series.getImageUrl(),
                series.getImdbRating(),
                series.getSeasonsNumber(),
                series.getGenre().getName(),
                series.isPromoted()
        );
    }
}
