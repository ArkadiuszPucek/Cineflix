package pl.puccini.cineflix.domain.series.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.genre.GenreFacade;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFacade;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.season.seasonDto.SeasonDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.UserUtils;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/series")
public class SeriesController {
    private final GenreFacade genreFacade;
    private final UserUtils userUtils;
    private final EpisodeFacade episodeFacade;
    private final SeriesFacade seriesFacade;

    public SeriesController(GenreFacade genreFacade, UserUtils userUtils, EpisodeFacade episodeFacade, SeriesFacade seriesFacade) {
        this.genreFacade = genreFacade;
        this.userUtils = userUtils;
        this.episodeFacade = episodeFacade;
        this.seriesFacade = seriesFacade;
    }

    @GetMapping
    public String seriesPage(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        List<Genre> allGenres = genreFacade.getAllGenres();
        List<Genre> filteredGenres = allGenres.stream()
                .filter(seriesGenre -> seriesFacade.getNumberOfMoviesByGenre(seriesGenre) > 1).toList();
        model.addAttribute("genres", filteredGenres);

        List<SeriesDto> allSeriesInService = seriesFacade.findAllSeries(userId);
        model.addAttribute("allSeriesInService", allSeriesInService);

        return "series";
    }


    @GetMapping("/{genre}")
    private String getSeriesByGenre(@PathVariable String genre, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        String capitalizedGenre = Character.toUpperCase(genre.charAt(0)) + genre.substring(1);

        List<SeriesDto> seriesByGenre = seriesFacade.getSeriesByGenre(capitalizedGenre, userId);

        List<Genre> allGenres = genreFacade.getAllGenres();
        List<Genre> filteredGenres = allGenres.stream()
                .filter(seriesGenre -> seriesFacade.getNumberOfMoviesByGenre(seriesGenre) > 1).toList();
        model.addAttribute("genres", filteredGenres);

        model.addAttribute("genre", capitalizedGenre);
        model.addAttribute("seriesByGenre", seriesByGenre);
        return "seriesByGenre";
    }


    @GetMapping("/{title}/sezon-{seasonNumber}")
    public String showSeriesPage(@PathVariable String title, @PathVariable int seasonNumber, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        String searchingFormatTitle = title.toLowerCase().replace("-", " ");

        SeriesDto seriesDto = seriesFacade.findSeriesByTitle(searchingFormatTitle, userId);
        if (seriesDto == null) {
            return "error/not-found";
        }

        List<SeasonDto> seasons = seriesFacade.getSeasonsForSeries(seriesDto.getImdbId());

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

        EpisodeDto firstUnwatchedEpisode = episodeFacade.findFirstUnwatchedEpisode(seriesDto.getImdbId(), userId);
        model.addAttribute("watchedEpisodes", firstUnwatchedEpisode);

        List<EpisodeDto> episodes = seriesFacade.getEpisodesForSeason(selectedSeason.getId(), userId);
        model.addAttribute("episodes", episodes);

        String genre = seriesDto.getGenre();
        List<SeriesDto> seriesByGenre = seriesFacade.getSeriesByGenre(genre, userId);
        model.addAttribute("genre", seriesByGenre);

        model.addAttribute("series", seriesDto);
        model.addAttribute("selectedSeason", selectedSeason);

        return "series-title";
    }

}
