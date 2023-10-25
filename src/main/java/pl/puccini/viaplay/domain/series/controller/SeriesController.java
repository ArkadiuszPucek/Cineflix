package pl.puccini.viaplay.domain.series.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.imdb.IMDbApiService;
import pl.puccini.viaplay.domain.imdb.IMDbData;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.seasonDto.SeasonDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;
import pl.puccini.viaplay.domain.series.service.EpisodeService;
import pl.puccini.viaplay.domain.series.service.SeriesService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/series")
public class SeriesController {
    private final SeriesRepository seriesRepository;
    private final SeriesService seriesService;
    private final EpisodeService episodeService;
    private final GenreService genreService;
    private final IMDbApiService imdbApiService;

    public SeriesController(SeriesRepository seriesRepository, SeriesService seriesService, EpisodeService episodeService, GenreService genreService, IMDbApiService imdbApiService) {
        this.seriesRepository = seriesRepository;
        this.seriesService = seriesService;
        this.episodeService = episodeService;
        this.genreService = genreService;
        this.imdbApiService = imdbApiService;
    }
    @GetMapping
    public String seriesPage(){
        return "series";
    }

    @GetMapping("/add")
    public String showAddSeriesForm(Model model) {
        model.addAttribute("series", new Series());
        return "add-series";
    }

    @PostMapping("/add")
    public String addSeries(@ModelAttribute Series series) throws IOException, InterruptedException {
        // Zapisz serial w bazie danych
        seriesRepository.save(series);

        // Pobierz dane IMDb na podstawie IMDb ID
        IMDbData imdbData = imdbApiService.fetchIMDbData(series.getImdbId());

        // Zaktualizuj dane serialu na podstawie danych IMDb
        series.setImageUrl(imdbData.getImdbUrl());
        series.setImageUrl(imdbData.getImdbUrl());
        seriesRepository.save(series);
        return "redirect:/series";
    }

    @GetMapping("/{genre}")
    private String getSeriesByGenre(@PathVariable String genre, Model model){
            Genre genreByType = genreService.getGenreByType(genre);
            List<SeriesDto> seriesByGenre = seriesService.getSeriesByGenre(genreByType);
            model.addAttribute("genre", genre);
            model.addAttribute("seriesByGenre", seriesByGenre);
            return "redirect:/series/" + genre;
    }

    @GetMapping("/{title}/sezon-{seasonNumber}")
    public String showSeriesPage(@PathVariable String title, @PathVariable int seasonNumber, Model model) {
        String normalizedTitle = title.replace("-", " ").toLowerCase();
        SeriesDto seriesDto = seriesService.findByTitle(normalizedTitle);
        if (seriesDto == null) {
            return "series-not-found"; // Obsłuż przypadek, gdy serial nie istnieje
        }

        // Pobierz informacje o sezonach serialu
        List<SeasonDto> seasons = seriesService.getSeasonsForSeries(seriesDto.getImdbId());
        model.addAttribute("seasons", seasons);
        model.addAttribute("title", title);

        SeasonDto seasonDto = new SeasonDto();
        seasonDto.setSeasonNumber(seasonNumber); // Ustaw numer sezonu
        model.addAttribute("seasonDto", seasonDto);

        // Znajdź wybrany sezon na podstawie seasonNumber
        SeasonDto selectedSeason = seasons.stream()
                .filter(season -> season.getSeasonNumber() == seasonNumber)
                .findFirst()
                .orElse(null);

        if (selectedSeason == null) {
            return "season-not-found"; // Obsłuż przypadek, gdy wybrany sezon nie istnieje
        }

        // Pobierz epizody wybranego sezonu
        List<EpisodeDto> episodes = seriesService.getEpisodesForSeason(selectedSeason.getId());
        model.addAttribute("episodes", episodes);

        String genre = seriesDto.getGenre();
        Genre genreByType = genreService.getGenreByType(genre);
        List<SeriesDto> seriesByGenre = seriesService.getSeriesByGenre(genreByType);
        model.addAttribute("genre", seriesByGenre);

        // Dodaj pozostałe informacje do modelu
        model.addAttribute("series", seriesDto);
        model.addAttribute("selectedSeason", selectedSeason);

        return "series-title"; // Wyświetl widok serialu z epizodami wybranego sezonu
    }

}
