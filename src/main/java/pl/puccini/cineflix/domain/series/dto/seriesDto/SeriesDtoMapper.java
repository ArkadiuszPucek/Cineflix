package pl.puccini.cineflix.domain.series.dto.seriesDto;

import pl.puccini.cineflix.domain.series.model.Series;

public class SeriesDtoMapper {
    public static SeriesDto map(Series series) {
        return new SeriesDto(
                series.getImdbId(),
                series.getTitle(),
                series.getReleaseYear(),
                series.getImageUrl(),
                series.getBackgroundImageUrl(),
                series.getDescription(),
                series.getStaff(),

                series.getGenre().getGenreType(),
                series.isPromoted(),
                series.getAgeLimit(),
                series.getImdbRating(),
                series.getImdbUrl(),
                series.getSeasonsCount()
        );
    }
}
