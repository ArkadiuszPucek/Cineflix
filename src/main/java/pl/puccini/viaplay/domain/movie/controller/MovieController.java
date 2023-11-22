package pl.puccini.viaplay.domain.movie.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.service.MovieService;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final GenreService genreService;

    public MovieController(MovieService movieService, GenreService genreService) {
        this.movieService = movieService;
        this.genreService = genreService;
    }

    @GetMapping("/{value}")
    public String movieValueHandle(@PathVariable String value, Model model, HttpServletResponse response) {
        String normalizedTitle = value.replace("-", " ").toLowerCase();
        MovieDto movieDto = movieService.findMovieByTitle(normalizedTitle);
        if (movieDto != null) {
            model.addAttribute("title", value);

            Genre genreByType = genreService.getGenreByType(movieDto.getGenre());
            List<MovieDto> movieByGenre = movieService.getMovieByGenre(genreByType);
            model.addAttribute("moviesByGenre", movieByGenre);

            model.addAttribute("movie", movieDto);

            response.setStatus(HttpServletResponse.SC_OK);
            return "movie-title";
        } else {
            String capitalizedGenre = Character.toUpperCase(value.charAt(0)) + value.substring(1);
            Genre genreByType = genreService.getGenreByType(capitalizedGenre);

            if (genreByType != null) {
                model.addAttribute("genre", capitalizedGenre);

                List<MovieDto> moviesByGenre = movieService.getMovieByGenre(genreByType);
                model.addAttribute("moviesByGenre", moviesByGenre);

                List<Genre> allGenres = genreService.getAllGenres();
                model.addAttribute("genres", allGenres);
                response.setStatus(HttpServletResponse.SC_OK);

                return "moviesByGenre";
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "error/not-found";
    }

    @GetMapping
    public String moviesPage(Model model) {
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        String thrillerGenre = "Thriller";
        model.addAttribute("thrillerMoviesTitle", "Filmy akcji");
        model.addAttribute("thrillerMovies", getMoviesByGenre(thrillerGenre));
        model.addAttribute("thrillerGenre", thrillerGenre.toLowerCase());

        return "movies";
    }

    private List<MovieDto> getMoviesByGenre(String genre) {
        Genre genreByType = genreService.getGenreByType(genre);
        return movieService.getMovieByGenre(genreByType);
    }


}
