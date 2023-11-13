package pl.puccini.viaplay.domain.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.service.MovieService;
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
    public String addMovie(MovieDto movie) throws IOException, InterruptedException {
        movieService.addMovieManual(movie);
        String normalizedTitle = movie.getTitle().toLowerCase().replace(" ", "-");

        return "redirect:/movie/" + normalizedTitle;
    }

    @GetMapping("/add-movie-form")
    public String showAddMovieForm(Model model) {
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/add-movie-form";
    }

    @PostMapping("/add-movie-api")
    public String addMovieByApi(MovieDto movie) throws IOException, InterruptedException {
        Movie movieFromApi = movieService.addMovieByApi(movie);
        String normalizedTitle = movieFromApi.getTitle().toLowerCase().replace(" ", "-");

        return "redirect:/movies/" + normalizedTitle;
    }

    @GetMapping("/add-movie-api")
    public String showAddMovieApiForm(Model model) {
        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/add-movie-api";
    }


    @GetMapping("/manage-movies")
    public String showManageMovieForm(Model model) {

        return "admin/manage-movies";
    }


    @GetMapping("/add-series")
    public String showAddSeriesForm(Model model) {


        return "admin/add-series";
    }

    @GetMapping("/manage-series")
    public String showManageSeriesForm(Model model) {

        return "admin/manage-series";
    }

    @GetMapping("/manage-users")
    public String showManageUsersForm(Model model) {

        return "admin/manage-users";
    }

}
