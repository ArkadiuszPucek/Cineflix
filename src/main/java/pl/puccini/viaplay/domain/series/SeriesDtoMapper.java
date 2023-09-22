package pl.puccini.viaplay.domain.series;

public class SeriesDtoMapper {
    public static SeriesDto map(Series series) {
        return new SeriesDto(
                series.getImdbId(),
                series.getTitle(),
                series.getReleaseYear(),
                series.getImageUrl(),
                series.getDescription(),
                series.getCast(),
                series.getLanguages(),
                series.getGenre().getName(),
                series.isPromoted(),
                series.getSeasons().size(),
                series.getAgeLimit()
        );
    }
}
