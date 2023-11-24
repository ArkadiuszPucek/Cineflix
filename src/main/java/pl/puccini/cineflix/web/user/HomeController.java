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
import pl.puccini.cineflix.domain.user.service.UserService;
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

    HomeController(MovieService movieService, GenreService genreService, SeriesService seriesService, UserUtils userUtils) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.seriesService = seriesService;
        this.userUtils = userUtils;
    }

    @GetMapping("/")
    String home(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        model.addAttribute("randomPromotedItems", getRandomPromotedItem());

        model.addAttribute("seriesPromoBoxMainTitle", "Wciągające seriale ze szpitalnych korytarzy");
        model.addAttribute("seriesPromoBox", getSeriesPromoBox());

        model.addAttribute("moviePromoBoxMainTitle", "Filmy zyskujące popularność");
        model.addAttribute("moviePromoBox", getMoviePromoBox());

        String dramaGenre =  "Drama";
        model.addAttribute("dramaSeriesTitle", "Seriale dramatyczne");
        model.addAttribute("dramaSeries", getSeriesByGenre(dramaGenre));
        model.addAttribute("dramaGenre", dramaGenre.toLowerCase());

        String comedyGenre =  "Comedy";
        model.addAttribute("comedySeriesTitle", "Seriale komediowe");
        model.addAttribute("comedySeries", getSeriesByGenre(comedyGenre));
        model.addAttribute("comedyGenre", comedyGenre.toLowerCase());

        String actionGenre =  "Action";
        model.addAttribute("actionSeriesTitle", "Seriale akcji");
        model.addAttribute("actionSeries", getSeriesByGenre(actionGenre));
        model.addAttribute("actionGenre", actionGenre.toLowerCase());

        String thrillerGenre =  "Thriller";
        model.addAttribute("thrillerMoviesTitle", "Filmy akcji");
        model.addAttribute("thrillerMovies", getMoviesByGenre(thrillerGenre));
        model.addAttribute("thrillerGenre", thrillerGenre.toLowerCase());
        return "index";
    }


    private List<SeriesDto> getSeriesByGenre(String genre) {
        Genre genreByType = genreService.getGenreByType(genre);
        return seriesService.getSeriesByGenre(genreByType);
    }

    private List<MovieDto> getMoviesByGenre(String genre) {
        Genre genreByType = genreService.getGenreByType(genre);
        return movieService.getMovieByGenre(genreByType);
    }

    private Object getRandomPromotedItem() {
        List<MovieDto> allPromotedMovies = movieService.findAllPromotedMovies();
        List<SeriesDto> allPromotedSeries = seriesService.findAllPromotedMovies();

        List<Object> promotedItems = new ArrayList<>();
        promotedItems.addAll(allPromotedMovies);
        promotedItems.addAll(allPromotedSeries);

        int size = promotedItems.size();
        if (size > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(size);
            return promotedItems.get(randomIndex);
        } else {
//            TO DO
            return null;
        }
    }

    private List<SeriesDto> getSeriesPromoBox() {
        return Stream.of("tt7817340", "tt6470478", "tt4655480", "tt6236572", "tt6664638")
                .flatMap(imdbId -> seriesService.getSeriesByImdbId(imdbId).stream())
                .collect(Collectors.toList());
    }

    private List<MovieDto> getMoviePromoBox() {
        return Stream.of("tt0993842", "tt4034228", "tt2304933", "tt6644200", "tt6146586")
                .flatMap(imdbId -> movieService.getMoviesByImdbId(imdbId).stream())
                .collect(Collectors.toList());
    }
}
