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
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.List;

@Controller
@RequestMapping("/series")
public class SeriesController {
    private final SeriesService seriesService;
    private final GenreService genreService;
    private final UserUtils userUtils;
    private final EpisodeService episodeService;

    public SeriesController(SeriesService seriesService, GenreService genreService, UserUtils userUtils, EpisodeService episodeService) {
        this.seriesService = seriesService;
        this.genreService = genreService;
        this.userUtils = userUtils;
        this.episodeService = episodeService;
    }


    @GetMapping
    public String seriesPage(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<SeriesDto> allSeriesInService = seriesService.findAllSeriesInService(userId);
        model.addAttribute("allSeriesInService", allSeriesInService);

        String dramaGenre = "Drama";
        model.addAttribute("dramaSeriesTitle", "Drama series");
        model.addAttribute("dramaSeries", seriesService.getSeriesByGenre(dramaGenre, userId));
        model.addAttribute("dramaGenre", dramaGenre.toLowerCase());

        String comedyGenre = "Comedy";
        model.addAttribute("comedySeriesTitle", "Comedy series");
        model.addAttribute("comedySeries", seriesService.getSeriesByGenre(comedyGenre, userId));
        model.addAttribute("comedyGenre", comedyGenre.toLowerCase());

        String actionGenre = "Action";
        model.addAttribute("actionSeriesTitle", "Action series");
        model.addAttribute("actionSeries", seriesService.getSeriesByGenre(actionGenre, userId));
        model.addAttribute("actionGenre", actionGenre.toLowerCase());
        return "series";
    }


    @GetMapping("/{genre}")
    private String getSeriesByGenre(@PathVariable String genre, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        String capitalizedGenre = Character.toUpperCase(genre.charAt(0)) + genre.substring(1);

        List<SeriesDto> seriesByGenre = seriesService.getSeriesByGenre(capitalizedGenre, userId);

        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        model.addAttribute("genre", capitalizedGenre);
        model.addAttribute("seriesByGenre", seriesByGenre);
        return "seriesByGenre";
    }


    @GetMapping("/{title}/sezon-{seasonNumber}")
    public String showSeriesPage(@PathVariable String title, @PathVariable int seasonNumber, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        String normalizedTitle = title.replace("-", " ").toLowerCase();
        SeriesDto seriesDto = seriesService.findSeriesByTitle(normalizedTitle, userId);
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

        EpisodeDto firstUnwatchedEpisode = episodeService.findFirstUnwatchedEpisode(seriesDto.getImdbId(), userId);
        model.addAttribute("watchedEpisodes", firstUnwatchedEpisode);

        List<EpisodeDto> episodes = seriesService.getEpisodesForSeason(selectedSeason.getId(), userId);
        model.addAttribute("episodes", episodes);

        String genre = seriesDto.getGenre();
        List<SeriesDto> seriesByGenre = seriesService.getSeriesByGenre(genre, userId);
        model.addAttribute("genre", seriesByGenre);

        model.addAttribute("series", seriesDto);
        model.addAttribute("selectedSeason", selectedSeason);

        return "series-title";
    }

}
