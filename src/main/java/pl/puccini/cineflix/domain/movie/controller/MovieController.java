package pl.puccini.cineflix.domain.movie.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final GenreService genreService;
    private final UserUtils userUtils;

    public MovieController(MovieService movieService, GenreService genreService, UserUtils userUtils) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.userUtils = userUtils;
    }

    @GetMapping("/{value}")
    public String movieValueHandle(@PathVariable String value, Model model, Authentication authentication, HttpServletResponse response) {
        userUtils.addAvatarUrlToModel(authentication, model);
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
    public String moviesPage(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
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
