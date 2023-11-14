package pl.puccini.viaplay.domain.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.service.MovieService;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.viaplay.domain.series.service.SeriesService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class AdminController {
    private final MovieService movieService;

    private final SeriesService seriesService;
    private final GenreService genreService;

    public AdminController(MovieService movieService, SeriesService seriesService, GenreService genreService) {
        this.movieService = movieService;
        this.seriesService = seriesService;
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
    public String deleteMovie(@PathVariable String imdbId, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = movieService.deleteMovieByImdbId(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Film został pomyślnie usunięty.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono filmu do usunięcia.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania filmu: " + e.getMessage());
        }
        return "redirect:/manage-movies";
    }


//SERIES

//    @PostMapping("/add-series")
//    public String addSeries(@ModelAttribute SeriesDto seriesDto, Model model, RedirectAttributes redirectAttributes) {
//        try {
//            // Tworzenie obiektu Series bez szczegółów sezonów
//            seriesService.addSeriesManual(seriesDto);
//            // Przekierowanie do dodawania informacji o sezonach
//            return "redirect:/admin/add-season/" + seriesDto.getImdbId() + "/1"; // Przekierowanie do sezonu 1
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Błąd podczas dodawania serialu: " + e.getMessage());
//            return "redirect:/admin/add-series";
//        }
//    }
//    @PostMapping("/add-series-form")
//    public String addSeriesManual(SeriesDto series, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
//        if (seriesService.existsByImdbId(series.getImdbId())) {
//            redirectAttributes.addFlashAttribute("error", "Serial o podanym IMDb id istnieje w serwisie!");
//            return "redirect:/add-series-form";
//        }
//        seriesService.addSeriesManual(series);
//        String normalizedTitle = series.getTitle().toLowerCase().replace(" ", "-");
//
//        return "redirect:/series/" + normalizedTitle +"/sezon-1";
//    }


    @PostMapping("/add-series-form")
    public String addSeriesManual(@ModelAttribute SeriesDto series, Model model, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        if (seriesService.existsByImdbId(series.getImdbId())) {
            redirectAttributes.addFlashAttribute("error", "Serial o podanym IMDb id istnieje w serwisie!");
            return "redirect:/add-series-form";
        }
        try {
            // Tworzenie obiektu Series bez szczegółów sezonów
            seriesService.addSeriesManual(series);
            // Przekierowanie do dodawania informacji o sezonach
            return "redirect:/admin/add-season/" + series.getImdbId() + "/1"; // Przekierowanie do sezonu 1
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Błąd podczas dodawania serialu: " + e.getMessage());
            return "redirect:/admin/add-series";
        }
    }

    @PostMapping("/add-season/{imdbId}/{seasonNumber}")
    public String addSeason(@PathVariable String imdbId, @PathVariable int seasonNumber, @ModelAttribute SeasonDto seasonDto, RedirectAttributes redirectAttributes) {
        // Logika dodawania sezonu do serialu
        // ...
        // Sprawdzenie, czy to był ostatni sezon
        if (seasonNumber == seriesService.getSeasonsCount(imdbId)) {
            // Przekierowanie do listy seriali
            redirectAttributes.addFlashAttribute("success", "Wszystkie sezony zostały dodane.");
            return "redirect:/admin/manage-series";
        } else {
            // Przekierowanie do dod
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


    @GetMapping("/manage-users")
    public String showManageUsersForm(Model model) {

        return "admin/series/manage-users";
    }

}
