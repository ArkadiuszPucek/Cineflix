package pl.puccini.viaplay.domain.movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.imdb.IMDbApiService;
import pl.puccini.viaplay.domain.imdb.IMDbData;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.repository.MovieRepository;
import pl.puccini.viaplay.domain.movie.service.MovieService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final GenreService genreService;
    private final IMDbApiService imdbApiService;

    public MovieController(MovieRepository movieRepository, MovieService movieService, GenreService genreService, IMDbApiService imdbApiService) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
        this.genreService = genreService;
        this.imdbApiService = imdbApiService;
    }


    @GetMapping("/add")
    public String showAddMovieForm(Model model) {
        Movie movie = new Movie();
        model.addAttribute("movie", movie);

        return "add-movie";
    }

    @PostMapping("/add")
    public String addMovie(@ModelAttribute Movie movie) throws IOException, InterruptedException {
        if (movie.getImdbId() != null && !movie.getImdbId().isEmpty()) {
            // Pobierz dane IMDb na podstawie IMDb ID
            IMDbData imdbData = imdbApiService.fetchIMDbData(movie.getImdbId());

            // Zaktualizuj dane filmu na podstawie danych IMDb
            movie.setImdbRating(imdbData.getImdbRating());
            movie.setImdbUrl(imdbData.getImdbUrl());
        }

        movieRepository.save(movie);

        return "redirect:/movies";
    }

//    @GetMapping("/{title}")
//    public String showSeriesPage(@PathVariable String title, Model model) {
//        String normalizedTitle = title.replace("-", " ").toLowerCase();
//        MovieDto movieDto = movieService.findMovieByTitle(normalizedTitle);
//        if (movieDto == null) {
//            return "not-found";
//        }
//        model.addAttribute("title", title);
//
//        int releaseYear = movieDto.getReleaseYear();
//
//
//        Genre genreByType = genreService.getGenreByType(movieDto.getGenre());
//        List<MovieDto> movieByGenre = movieService.getMovieByGenre(genreByType);
//        model.addAttribute("moviesByGenre", movieByGenre);
//
//        // Dodaj pozostałe informacje do modelu
//        model.addAttribute("movie", movieDto);
//
//        return "movie-title"; // Wyświetl widok serialu z epizodami wybranego sezonu
//    }

    @GetMapping("/{genre}")
    private String getMoviesByGenre(@PathVariable String genre, Model model){
        // Konwersja pierwszej litery parametru na wielką literę
        String capitalizedGenre = Character.toUpperCase(genre.charAt(0)) + genre.substring(1);
        Genre genreByType = genreService.getGenreByType(capitalizedGenre);
        model.addAttribute("genre", capitalizedGenre);

        List<MovieDto> movieByGenre = movieService.getMovieByGenre(genreByType);
        model.addAttribute("moviesByGenre", movieByGenre);

        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        return "moviesByGenre";
    }

    @GetMapping
    public String moviesPage(Model model){
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        String thrillerGenre =  "Thriller";
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
