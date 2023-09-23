package pl.puccini.viaplay.domain.series.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.puccini.viaplay.domain.imdb.IMDbApiService;
import pl.puccini.viaplay.domain.imdb.IMDbData;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.repository.SeriesRepository;

import java.io.IOException;

@Controller
@RequestMapping("/series")
public class SeriesController {
    private final SeriesRepository seriesRepository;
    private final IMDbApiService imdbApiService;

    public SeriesController(SeriesRepository seriesRepository, IMDbApiService imdbApiService) {
        this.seriesRepository = seriesRepository;
        this.imdbApiService = imdbApiService;
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

}
