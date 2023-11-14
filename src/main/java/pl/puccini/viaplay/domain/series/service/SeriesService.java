package pl.puccini.viaplay.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreRepository;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.viaplay.domain.series.dto.seasonDto.SeasonDto;
import pl.puccini.viaplay.domain.series.dto.seasonDto.SeasonDtoMapper;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDtoMapper;
import pl.puccini.viaplay.domain.series.model.Episode;
import pl.puccini.viaplay.domain.series.model.Season;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.repository.SeasonRepository;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final SeasonRepository seasonRepository;
    private final GenreRepository genreRepository;

    public SeriesService(SeriesRepository seriesRepository, SeasonRepository seasonRepository, GenreRepository genreRepository) {
        this.seriesRepository = seriesRepository;
        this.seasonRepository = seasonRepository;
        this.genreRepository = genreRepository;
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

    public List<SeriesDto> searchSeries(String query) {
        String loweredQuery = query.toLowerCase();
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        return seriesRepository.findByTitleContainingIgnoreCaseOrStaffContainingIgnoreCase(loweredQuery, loweredQuery).stream()
                .map(SeriesDtoMapper::map)
                .toList();
    }

    public List<SeasonDto> getSeasonsForSeries(String imdbId) {
        // Pobierz listę sezonów na podstawie imdbId
        List<Season> seasons = seasonRepository.findSeasonsBySeriesImdbId(imdbId);

        // Mapuj sezon na SeasonDto za pomocą SeasonDtoMapper
        List<SeasonDto> seasonDtos = seasons.stream()
                .map(SeasonDtoMapper::map)
                .collect(Collectors.toList());

        return seasonDtos;
    }

    public List<EpisodeDto> getEpisodesForSeason(Long seasonId) {
        Season season = seasonRepository.findById(seasonId).orElse(null);
        if (season == null) {
            // Obsłuż przypadek, gdy sezon nie istnieje
            return Collections.emptyList();
        }

        List<Episode> episodes = season.getEpisodes();
        List<EpisodeDto> episodeDtos = new ArrayList<>();

        for (Episode episode : episodes) {
            EpisodeDto episodeDto = EpisodeDtoMapper.map(episode);
            episodeDtos.add(episodeDto);
        }

        return episodeDtos;
    }

    public boolean existsByImdbId(String imdbId) {
        return seriesRepository.existsByImdbId(imdbId);
    }

    public void addSeriesManual(SeriesDto seriesDto) throws IOException, InterruptedException {
        Series series = new Series();
        series.setImdbId(seriesDto.getImdbId());
        series.setTitle(seriesDto.getTitle());
        series.setReleaseYear(seriesDto.getReleaseYear());
        series.setImageUrl(seriesDto.getImageUrl());
        series.setBackgroundImageUrl(seriesDto.getBackgroundImageUrl());
        series.setDescription(seriesDto.getDescription());
        series.setStaff(seriesDto.getStaff());
        series.setLanguages(seriesDto.getLanguages());
        series.setGenre(genreRepository.findByGenreTypeIgnoreCase(seriesDto.getGenre()));
        series.setPromoted(seriesDto.isPromoted());
        series.setAgeLimit(seriesDto.getAgeLimit());
        series.setImdbRating(seriesDto.getImdbRating());
        series.setImdbUrl("https://www.imdb.com/title/"+seriesDto.getImdbId());
        series.setSeasonsCount(seriesDto.getSeasonsCount());
        seriesRepository.save(series);
    }

}
