package pl.puccini.viaplay.domain.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.service.MovieService;
import pl.puccini.viaplay.domain.series.service.SeriesService;

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


//    @PostMapping("/add")
//    public String addMovie(@ModelAttribute Movie movie) throws IOException, InterruptedException {
//        if (movie.getImdbId() != null && !movie.getImdbId().isEmpty()) {
//            // Pobierz dane IMDb na podstawie IMDb ID
//            IMDbData imdbData = imdbApiService.fetchIMDbData(movie.getImdbId());
//
//            // Zaktualizuj dane filmu na podstawie danych IMDb
//            movie.setImdbRating(imdbData.getImdbRating());
//            movie.setImdbUrl(imdbData.getImdbUrl());
//        }
//
//        movieRepository.save(movie);
//
//        return "redirect:/movies";
//    }

    @GetMapping("/admin")
    public String getAdminPanel() {

        return "admin/admin";
    }

    @GetMapping("/add-movie")
    public String showAddMovieForm(Model model) {
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);


        return "admin/add-movie";
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
