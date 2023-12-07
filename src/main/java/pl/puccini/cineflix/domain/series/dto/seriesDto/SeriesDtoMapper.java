package pl.puccini.cineflix.domain.series.dto.seriesDto;

import pl.puccini.cineflix.domain.series.model.Series;

public class SeriesDtoMapper {
    public static SeriesDto map(Series series) {
        SeriesDto seriesDto = new SeriesDto();
        seriesDto.setImdbId(series.getImdbId());
        seriesDto.setTitle(series.getTitle());
        seriesDto.setReleaseYear(series.getReleaseYear());
        seriesDto.setImageUrl(series.getImageUrl());
        seriesDto.setBackgroundImageUrl(series.getBackgroundImageUrl());
        seriesDto.setDescription(series.getDescription());
        seriesDto.setStaff(series.getStaff());
        seriesDto.setGenre(series.getGenre().getGenreType());
        seriesDto.setPromoted(series.isPromoted());
        seriesDto.setAgeLimit(series.getAgeLimit());
        seriesDto.setImdbRating(series.getImdbRating());
        seriesDto.setImdbUrl(series.getImdbUrl());
        seriesDto.setSeasonsCount(series.getSeasons().size());
        seriesDto.setOnUserList(null);
        seriesDto.setUserRating(null);
        seriesDto.setFirstUnwatchedEpisodeId(null);
        seriesDto.setRateUpCount(0);
        seriesDto.setRateDownCount(0);
        return seriesDto;
    }
}
