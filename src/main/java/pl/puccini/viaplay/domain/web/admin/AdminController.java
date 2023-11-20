package pl.puccini.viaplay.domain.web.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.viaplay.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.viaplay.domain.exceptions.SeriesAlreadyExistsException;
import pl.puccini.viaplay.domain.exceptions.SeriesNotFoundException;
import pl.puccini.viaplay.domain.exceptions.SeriesUpdateException;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.service.MovieService;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.model.Episode;
import pl.puccini.viaplay.domain.series.model.Series;
import pl.puccini.viaplay.domain.series.service.EpisodeService;
import pl.puccini.viaplay.domain.series.service.SeriesService;

import java.io.IOException;
import java.util.*;


@Controller
public class AdminController {
    private final MovieService movieService;
    private final SeriesService seriesService;
    private final EpisodeService episodeService;
    private final GenreService genreService;

    public AdminController(MovieService movieService, SeriesService seriesService, EpisodeService episodeService, GenreService genreService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
        this.episodeService = episodeService;
        this.genreService = genreService;
    }


    @GetMapping("/admin")
    public String getAdminPanel() {

        return "admin/admin";
    }

    @PostMapping("/add-movie-form")
    public String addMovieManual(MovieDto movie, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        if (movieService.existsByImdbId(movie.getImdbId())) {
            redirectAttributes.addFlashAttribute("error", "Film o podanym IMDb id istnieje w serwisie!");
            return "redirect:/add-movie-form";
        }
        movieService.addMovieManual(movie);
        String normalizedTitle = movie.getTitle().toLowerCase().replace(" ", "-");

        return "redirect:/movies/" + normalizedTitle;
    }

    @GetMapping("/add-movie-form")
    public String showAddMovieManualForm(Model model) {
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-form";
    }

    @PostMapping("/add-movie-api")
    public String addMovieByApi(MovieDto movie, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        if (movieService.existsByImdbId(movie.getImdbId())) {
            redirectAttributes.addFlashAttribute("error", "Film o podanym IMDb id istnieje w serwisie!");
            return "redirect:/add-movie-api";
        }
        Movie movieFromApi = movieService.addMovieByApi(movie);
        String normalizedTitle = movieFromApi.getTitle().toLowerCase().replace(" ", "-");

        return "redirect:/movies/" + normalizedTitle;
    }

    @GetMapping("/add-movie-api")
    public String showAddMovieApiForm(Model model) {
        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-api";
    }


    @GetMapping("/manage-movies")
    public String showManageMovieForm(Model model) {
        List<MovieDto> allMoviesInService = movieService.findAllMoviesInService();
        model.addAttribute("allMoviesInService", allMoviesInService);

        return "admin/movies/manage-movies";
    }

    @GetMapping("/edit-movie/{imdbId}")
    public String showEditMovieForm(@PathVariable String imdbId, Model model) {
        MovieDto movieByImdbId = movieService.findMovieByImdbId(imdbId);

        model.addAttribute("movie", movieByImdbId);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/movies/edit-movie-form";
    }

    @PostMapping("/update-movie")
    public String updateMovie(@ModelAttribute("movie") MovieDto movieDto, RedirectAttributes redirectAttributes) {
        try {
            boolean updateResult = movieService.updateMovie(movieDto);

            if (updateResult) {
                redirectAttributes.addFlashAttribute("success", "Film został pomyślnie zaktualizowany.");
                return "redirect:/manage-movies";
            } else {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono filmu do aktualizacji.");
                return "redirect:/edit-movie/" + movieDto.getImdbId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas aktualizacji filmu: " + e.getMessage());
            return "redirect:/edit-movie/" + movieDto.getImdbId();
        }
    }

    @GetMapping("/delete-movie/{imdbId}")
    public String deleteMovie(@PathVariable String imdbId,
                              RedirectAttributes redirectAttributes,
                              @RequestParam String action) {
        if ("deleteMovie".equals(action)) {
            boolean deleted = movieService.deleteMovieByImdbId(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Film został pomyślnie usunięty.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono filmu do usunięcia.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania filmu");
        }
        return "redirect:/manage-movies";
    }


//SERIES


    @PostMapping("/add-series-form")
    public String addSeriesManual(@ModelAttribute SeriesDto series, RedirectAttributes redirectAttributes, HttpSession session) {
        if (seriesService.existsByImdbId(series.getImdbId())) {
            redirectAttributes.addFlashAttribute("message", "Film o podanym IMDb id istnieje w serwisie!");
            return "redirect:/add-series-form";
        }

        try {
            seriesService.addSeriesManual(series);
            session.setAttribute("seasonsCount", series.getSeasonsCount());
            session.setAttribute("title", series.getTitle());
            redirectAttributes.addFlashAttribute("seasonsCount", series.getSeasonsCount());
            redirectAttributes.addFlashAttribute("message", "Serial został dodany.");
            return "redirect:/add-episode/"+series.getImdbId()+"/1/1";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "Błąd podczas dodawania serialu: " + e.getMessage());
            return "redirect:/add-series-form";
        }
    }
    @GetMapping("/add-series-form")
    public String showAddSeriesManualForm(Model model) {
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        SeriesDto series = new SeriesDto();
        model.addAttribute("series", series);

        return "admin/series/add-series-form";
    }

    @GetMapping("/add-episode/{seriesId}/{seasonNumber}/{episodeNumber}")
    public String showAddEpisodeForm(@PathVariable String seriesId,
                                     @PathVariable int seasonNumber,
                                     @PathVariable int episodeNumber,
                                     Model model) {
        EpisodeDto episodeDto = new EpisodeDto();
        model.addAttribute("episode", episodeDto);
        model.addAttribute("seriesId", seriesId);
        model.addAttribute("seasonNumber", seasonNumber);
        model.addAttribute("episodeNumber", episodeNumber);
        return "admin/series/add-episode-form";
    }

    @PostMapping("/add-episode/{seriesId}/{seasonNumber}/{episodeNumber}")
    public String addEpisode(@ModelAttribute EpisodeDto episodeDto,
                             @PathVariable String seriesId,
                             @PathVariable int seasonNumber,
                             @PathVariable int episodeNumber,
                             HttpSession session,
                             @RequestParam String action,
                             RedirectAttributes redirectAttributes) {
        try {
            Integer seasonsCount = (Integer) session.getAttribute("seasonsCount");
            String seriesTitle = (String) session.getAttribute("title");

            if (seasonsCount == null) {
                redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas dodawnia liczby sezonów");
                return "redirect:/admin";
            }

            episodeDto.setEpisodeNumber(episodeNumber);
            String redirectUrl = episodeService.processEpisodeAddition(episodeDto, seriesId, seasonNumber, episodeNumber, seasonsCount, action, seriesTitle);
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd: " + e.getMessage());
            return "redirect:/admin/add-episode/" + seriesId + "/" + seasonNumber + "/" + episodeNumber;
        }
    }


    @PostMapping("/add-series-api")
    public String addSeriesByApi(SeriesDto series, RedirectAttributes redirectAttributes) {
        try {
            Series seriesFromApi = seriesService.addSeriesIfNotExist(series);
            String normalizedTitle = seriesService.getNormalizedSeriesTitle(seriesFromApi.getTitle());

            return "redirect:/series/" + normalizedTitle +"/sezon-1";
        } catch (SeriesAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/add-series-api";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd: " + e.getMessage());
            return "redirect:/add-series-api";
        }
    }

    @GetMapping("/add-series-api")
    public String showAddSeriesApiForm(Model model) {
        SeriesDto seriesDto = new SeriesDto();
        model.addAttribute("series", seriesDto);

        return "admin/series/add-series-api";
    }

    @GetMapping("/manage-series")
    public String showManageSeriesForm(Model model) {
        List<SeriesDto> allSeriesInService = seriesService.findAllMoviesInService();
        model.addAttribute("allSeriesInService", allSeriesInService);

        return "admin/series/manage-series";
    }

    @GetMapping("/edit-series/{imdbId}")
    public String showEditSeriesForm(@PathVariable String imdbId, Model model) {
        SeriesDto seriesByImdbId = seriesService.findSeriesByImdbId(imdbId);

        model.addAttribute("series", seriesByImdbId);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/series/edit-series-form";
    }

    @PostMapping("/update-series")
    public String updateSeries(@ModelAttribute("series") SeriesDto seriesDto, RedirectAttributes redirectAttributes) {
        try {
            seriesService.updateSeries(seriesDto);
            redirectAttributes.addFlashAttribute("success", "Serial został pomyślnie zaktualizowany.");
            return "redirect:/manage-series";
        } catch (SeriesNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manage-series";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas aktualizacji serialu: " + e.getMessage());
            return "redirect:/edit-series/" + seriesDto.getImdbId();
        }
    }

    @GetMapping("/delete-series/{imdbId}")
    public String deleteSeries(@PathVariable String imdbId,
                               RedirectAttributes redirectAttributes,
                               @RequestParam String action) {

        if ("deleteSeries".equals(action)) {
            boolean deleted = seriesService.deleteSeriesByImdbId(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Serial został pomyślnie usunięty.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono serialu do usunięcia.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania serialu: ");
        }
        return "redirect:/manage-series";
    }

    @GetMapping("/edit-series-seasons/{imdbId}")
    public String showManageSeasonsForm(@PathVariable String imdbId, Model model, HttpSession session) {
        Series series = seriesService.findSeriesByImdbIdSeriesType(imdbId);
        session.setAttribute("imdbId", imdbId);

        if (series == null){
            return "redirect:/manage-series";
        }

        model.addAttribute("series", series);
        model.addAttribute("seasons", series.getSeasons());
        return "admin/series/manage-seasons";
    }

    @GetMapping("/edit-episode/{episodeId}")
    public String showEpisodeEditForm(@PathVariable Long episodeId, Model model) {
        EpisodeDto episodeById = episodeService.getEpisodeById(episodeId);
        model.addAttribute("episode", episodeById);
        return "admin/series/edit-episode-form";
    }

    @PostMapping("/update-episode")
    public String updateSeries(@ModelAttribute("episode") EpisodeDto episodeDto,
                               RedirectAttributes redirectAttributes) {


        try {
            Episode updatedEpisode = episodeService.updateEpisode(episodeDto);
            String seriesImdbId = updatedEpisode.getSeason().getSeries().getImdbId();
            redirectAttributes.addFlashAttribute("message", "Epizod został pomyślnie zaktualizowany.");
            return "redirect:/edit-series-seasons/" + seriesImdbId;
        } catch (EpisodeNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas edytowania epizodu " + e.getMessage());
            return "redirect:/manage-series";
        }
    }

    @GetMapping("/delete-episode/{episodeId}")
    public String deleteEpisode(@PathVariable Long episodeId,
                               RedirectAttributes redirectAttributes,
                               @RequestParam String action) {

        try{
            if ("deleteEpisode".equals(action)){
                Episode episodeToDelete = episodeService.deleteEpisodeById(episodeId);
                String imdbId = episodeToDelete.getSeason().getSeries().getImdbId();
                redirectAttributes.addFlashAttribute("message", "Epizod został pomyślnie usunięty.");
                return "redirect:/edit-series-seasons/" + imdbId;
            }else {
                redirectAttributes.addFlashAttribute("message", "Epizod nie został usunięty.");
                return "redirect:/manage-series";
            }
        } catch (EpisodeNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas edytowania epizodu " + e.getMessage());
            return "redirect:/manage-series";
        }
    }




    @GetMapping("/manage-users")
    public String showManageUsersForm(Model model) {

        return "admin/series/manage-users";
    }



}
