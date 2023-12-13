package pl.puccini.cineflix.domain.movie.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {
    private final GenreService genreService;
    private final UserUtils userUtils;
    private final MovieFacade movieFacade;

    public MovieController(GenreService genreService, UserUtils userUtils, MovieFacade movieFacade) {
        this.genreService = genreService;
        this.userUtils = userUtils;
        this.movieFacade = movieFacade;
    }

    @GetMapping("/{value}")
    public String movieValueHandle(@PathVariable String value, Model model, Authentication authentication, HttpServletResponse response) {
        userUtils.addAvatarUrlToModel(authentication, model);
        String normalizedTitle = value.replace("-", " ").toLowerCase();
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        MovieDto movieDto = movieFacade.findMovieByTitle(normalizedTitle, userId);

        if (movieDto != null) {
            model.addAttribute("title", value);

            List<MovieDto> movieByGenre = movieFacade.getMovieByGenre(movieDto.getGenre(), userId);
            model.addAttribute("moviesByGenre", movieByGenre);

            model.addAttribute("movie", movieDto);

            response.setStatus(HttpServletResponse.SC_OK);
            return "movie-title";
        } else {
            String capitalizedGenre = Character.toUpperCase(value.charAt(0)) + value.substring(1);
            Genre genreByType = genreService.getGenreByType(capitalizedGenre);

            if (genreByType != null) {
                model.addAttribute("genre", capitalizedGenre);

                List<MovieDto> moviesByGenre = movieFacade.getMovieByGenre(capitalizedGenre, userId);
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
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<MovieDto> allMoviesInService = movieFacade.findAllMovies(userId);
        model.addAttribute("allMoviesInService", allMoviesInService);

        return "movies";
    }

    @GetMapping("/play-movie/{imdbId}")
    public String playMovie(@PathVariable String imdbId, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);

        MovieDto movieDto = movieFacade.getMovieDtoByImdbId(imdbId);

        if (movieDto == null) {
            return "error/not-found";
        }

        String youTubeUrl = userUtils.extractVideoId(movieDto.getMediaUrl());
        model.addAttribute("mediaUrl", youTubeUrl);
        model.addAttribute("movieTitle", movieDto.getTitle());
        return "movie-player";
    }


}
