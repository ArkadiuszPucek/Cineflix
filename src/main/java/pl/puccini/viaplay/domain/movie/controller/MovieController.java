package pl.puccini.viaplay.domain.movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.puccini.viaplay.domain.imdb.IMDbApiService;
import pl.puccini.viaplay.domain.imdb.IMDbData;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.movie.model.Movie;
import pl.puccini.viaplay.domain.movie.repository.MovieRepository;
import pl.puccini.viaplay.domain.movie.service.MovieService;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final IMDbApiService imdbApiService;

    public MovieController(MovieRepository movieRepository, MovieService movieService, IMDbApiService imdbApiService) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
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

    @GetMapping("/movies/{genre}")
    private String getSeriesByGenre(@PathVariable String genre, Model model){
        List<MovieDto> moviesByGenre = movieService.getMovieByGenre(genre);
        model.addAttribute("genre", genre);
        model.addAttribute("moviesByGenre", moviesByGenre);
        return "redirect:/movies/" + genre;
    }

}
