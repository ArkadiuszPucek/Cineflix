package pl.puccini.cineflix.web.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.user.service.UserListService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
class HomeController {

    private final MovieService movieService;
    private final GenreService genreService;
    private final SeriesService seriesService;
    private final UserUtils userUtils;
    private final UserListService userListService;

    HomeController(MovieService movieService, GenreService genreService, SeriesService seriesService, UserUtils userUtils, UserListService userListService) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.seriesService = seriesService;
        this.userUtils = userUtils;
        this.userListService = userListService;
    }
    @GetMapping("/")
    String home(Authentication authentication, Model model) {
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        userUtils.addAvatarUrlToModel(authentication, model);
        model.addAttribute("randomPromotedItems", getRandomPromotedItem(userId));

        model.addAttribute("seriesPromoBoxMainTitle", "Wciągające seriale ze szpitalnych korytarzy");
        model.addAttribute("seriesPromoBox", getSeriesPromoBox(userId));

        model.addAttribute("moviePromoBoxMainTitle", "Filmy zyskujące popularność");
        model.addAttribute("moviePromoBox", getMoviePromoBox(userId));

        String dramaGenre = "Drama";
        model.addAttribute("dramaSeriesTitle", "Seriale dramatyczne");
        model.addAttribute("dramaSeries", getSeriesByGenre(dramaGenre, userId));
        model.addAttribute("dramaGenre", dramaGenre.toLowerCase());

        String comedyGenre = "Comedy";
        model.addAttribute("comedySeriesTitle", "Seriale komediowe");
        model.addAttribute("comedySeries", getSeriesByGenre(comedyGenre, userId));
        model.addAttribute("comedyGenre", comedyGenre.toLowerCase());

        String actionGenre = "Action";
        model.addAttribute("actionSeriesTitle", "Seriale akcji");
        model.addAttribute("actionSeries", getSeriesByGenre(actionGenre, userId));
        model.addAttribute("actionGenre", actionGenre.toLowerCase());

        String thrillerGenre = "Thriller";
        model.addAttribute("thrillerMoviesTitle", "Filmy akcji");
        model.addAttribute("thrillerMovies", getMoviesByGenre(thrillerGenre, userId));
        model.addAttribute("thrillerGenre", thrillerGenre.toLowerCase());

        return "index";
    }


    private List<SeriesDto> getSeriesByGenre(String genre, Long userId) {
        Genre genreByType = genreService.getGenreByType(genre);
        List<SeriesDto> series = seriesService.getSeriesByGenre(genreByType);
        series.forEach(serie -> serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId())));
        return series;
    }


    private List<MovieDto> getMoviesByGenre(String genre, Long userId) {
        Genre genreByType = genreService.getGenreByType(genre);
        List<MovieDto> movies = movieService.getMovieByGenre(genreByType);
        movies.forEach(movie -> movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId())));
        return movies;
    }

    private Object getRandomPromotedItem(Long userId) {
        List<MovieDto> allPromotedMovies = movieService.findAllPromotedMovies();
        allPromotedMovies.forEach(movie -> movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId())));

        List<SeriesDto> allPromotedSeries = seriesService.findAllPromotedSeries();
        allPromotedSeries.forEach(series -> series.setOnUserList(userListService.isOnList(userId, series.getImdbId())));

        List<Object> promotedItems = new ArrayList<>();
        promotedItems.addAll(allPromotedMovies);
        promotedItems.addAll(allPromotedSeries);

        int size = promotedItems.size();
        if (size > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(size);
            return promotedItems.get(randomIndex);
        } else {
            return null;
        }
    }

    private List<SeriesDto> getSeriesPromoBox(Long userId) {
        return Stream.of("tt7817340", "tt6470478", "tt4655480", "tt6236572", "tt6664638")
                .flatMap(imdbId -> seriesService.getSeriesByImdbId(imdbId).stream())
                .peek(serie -> serie.setOnUserList(userListService.isOnList(userId, serie.getImdbId())))
                .collect(Collectors.toList());
    }

    private List<MovieDto> getMoviePromoBox(Long userId) {
        return Stream.of("tt0993842", "tt4034228", "tt2304933", "tt6644200", "tt6146586")
                .flatMap(imdbId -> movieService.getMoviesByImdbId(imdbId).stream())
                .peek(movie -> movie.setOnUserList(userListService.isOnList(userId, movie.getImdbId())))
                .collect(Collectors.toList());
    }
}
