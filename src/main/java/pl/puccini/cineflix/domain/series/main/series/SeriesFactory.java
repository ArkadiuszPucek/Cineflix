package pl.puccini.cineflix.domain.series.main.series;

import org.springframework.stereotype.Component;
import pl.puccini.cineflix.domain.genre.repository.GenreRepository;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

@Component
public class SeriesFactory {
    private final GenreRepository genreRepository;

    public SeriesFactory(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Series createSeries(SeriesDto seriesDto, Boolean isPromoted) {
        Series series = new Series();
        updateSeriesWithDto(series, seriesDto);
        series.setImdbUrl("https://www.imdb.com/title/" + seriesDto.getImdbId());
        series.setSeasonsCount(seriesDto.getSeasonsCount());
        if (isPromoted != null) {
            series.setPromoted(isPromoted);
        }
        return series;
    }

    public void updateSeriesWithDto(Series series, SeriesDto seriesDto) {
        series.setImdbId(seriesDto.getImdbId());
        series.setTitle(seriesDto.getTitle());
        series.setReleaseYear(seriesDto.getReleaseYear());
        series.setImageUrl(seriesDto.getImageUrl());
        series.setBackgroundImageUrl(seriesDto.getBackgroundImageUrl());
        series.setDescription(seriesDto.getDescription());
        series.setStaff(seriesDto.getStaff());
        series.setGenre(genreRepository.findByGenreTypeIgnoreCase(seriesDto.getGenre()));
        series.setPromoted(seriesDto.isPromoted());
        series.setAgeLimit(seriesDto.getAgeLimit());
        series.setImdbRating(seriesDto.getImdbRating());
    }
}
