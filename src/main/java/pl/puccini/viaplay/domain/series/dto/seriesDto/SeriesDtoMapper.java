package pl.puccini.viaplay.domain.series.dto.seriesDto;

import pl.puccini.viaplay.domain.series.model.Series;

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
                series.getLanguages(),
                series.getGenre().getGenreType(),
                series.isPromoted(),
                series.getAgeLimit(),
                series.getImdbRating(),
                series.getImdbUrl(),
                series.getSeasonsCount()
        );
    }
}
