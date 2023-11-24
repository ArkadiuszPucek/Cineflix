package pl.puccini.cineflix.domain.series.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.seasonDto.SeasonDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.List;

@Controller
@RequestMapping("/series")
public class SeriesController {
    private final SeriesService seriesService;
    private final GenreService genreService;
    private final UserUtils userUtils;

    public SeriesController(SeriesService seriesService, GenreService genreService, UserUtils userUtils) {
        this.seriesService = seriesService;
        this.genreService = genreService;
        this.userUtils = userUtils;
    }


    @GetMapping
    public String seriesPage(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        String dramaGenre = "Drama";
        model.addAttribute("dramaSeriesTitle", "Seriale dramatyczne");
        model.addAttribute("dramaSeries", getSeriesByGenre(dramaGenre));
        model.addAttribute("dramaGenre", dramaGenre.toLowerCase());

        String comedyGenre = "Comedy";
        model.addAttribute("comedySeriesTitle", "Seriale komediowe");
        model.addAttribute("comedySeries", getSeriesByGenre(comedyGenre));
        model.addAttribute("comedyGenre", comedyGenre.toLowerCase());

        String actionGenre = "Action";
        model.addAttribute("actionSeriesTitle", "Seriale akcji");
        model.addAttribute("actionSeries", getSeriesByGenre(actionGenre));
        model.addAttribute("actionGenre", actionGenre.toLowerCase());
        return "series";
    }


    @GetMapping("/{genre}")
    private String getSeriesByGenre(@PathVariable String genre, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        String capitalizedGenre = Character.toUpperCase(genre.charAt(0)) + genre.substring(1);

        Genre genreByType = genreService.getGenreByType(capitalizedGenre);
        List<SeriesDto> seriesByGenre = seriesService.getSeriesByGenre(genreByType);

        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        model.addAttribute("genre", capitalizedGenre);
        model.addAttribute("seriesByGenre", seriesByGenre);
        return "seriesByGenre";
    }


    @GetMapping("/{title}/sezon-{seasonNumber}")
    public String showSeriesPage(@PathVariable String title, @PathVariable int seasonNumber, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        String normalizedTitle = title.replace("-", " ").toLowerCase();
        SeriesDto seriesDto = seriesService.findByTitle(normalizedTitle);
        if (seriesDto == null) {
            return "error/not-found";
        }

        List<SeasonDto> seasons = seriesService.getSeasonsForSeries(seriesDto.getImdbId());

        model.addAttribute("seasons", seasons);
        model.addAttribute("title", title);

        SeasonDto seasonDto = new SeasonDto();
        seasonDto.setSeasonNumber(seasonNumber);
        model.addAttribute("seasonDto", seasonDto);

        SeasonDto selectedSeason = seasons.stream()
                .filter(season -> season.getSeasonNumber() == seasonNumber)
                .findFirst()
                .orElse(null);

        if (selectedSeason == null) {
            return "error/not-found";
        }

        List<EpisodeDto> episodes = seriesService.getEpisodesForSeason(selectedSeason.getId());
        model.addAttribute("episodes", episodes);

        String genre = seriesDto.getGenre();
        Genre genreByType = genreService.getGenreByType(genre);
        List<SeriesDto> seriesByGenre = seriesService.getSeriesByGenre(genreByType);
        model.addAttribute("genre", seriesByGenre);

        model.addAttribute("series", seriesDto);
        model.addAttribute("selectedSeason", selectedSeason);

        return "series-title"; //
    }


    private List<SeriesDto> getSeriesByGenre(String genre) {
        Genre genreByType = genreService.getGenreByType(genre);
        return seriesService.getSeriesByGenre(genreByType);
    }
}
