package pl.puccini.viaplay;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.viaplay.domain.genre.Genre;
import pl.puccini.viaplay.domain.genre.GenreService;
import pl.puccini.viaplay.domain.movie.service.MovieService;
import pl.puccini.viaplay.domain.movie.dto.MovieDto;
import pl.puccini.viaplay.domain.series.service.SeriesService;
import pl.puccini.viaplay.domain.series.dto.seriesDto.SeriesDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
class HomeController {

    private final MovieService movieService;
    private final GenreService genreService;
    private final SeriesService seriesService;

    HomeController(MovieService movieService, GenreService genreService, SeriesService seriesService) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.seriesService = seriesService;
    }

    @GetMapping("/")
    String home(Model model){
        model.addAttribute("randomPromotedItems", getRandomPromotedItem());
        model.addAttribute("seriesPromoBoxMainTitle", "Wciągające seriale ze szpitalnych korytarzy");
        model.addAttribute("seriesPromoBox", getSeriesPromoBox());
        model.addAttribute("moviePromoBoxMainTitle", "Filmy zyskujące popularność");
        model.addAttribute("moviePromoBox", getMoviePromoBox());
        model.addAttribute("comedySeries", getSeriesByGenre("comedy"));
        return "index";
    }

    private List<SeriesDto> getSeriesByGenre(String genre){
        return seriesService.getSeriesByGenre(genre);

//        List<SeriesDto> processedComedySeries = comedySeries.stream()
//                .map(seriesDto -> {
//                    String genre = seriesDto.getGenre();
//                    if (genre != null) {
//                        genre = genre.replace(' ', '-').toLowerCase();
//                        seriesDto.setGenre(genre);
//                    }
//                    return seriesDto;
//                })
//                .collect(Collectors.toList());
//
//// Dodawanie przetworzonej listy do modelu
//        model.addAttribute("comedySeries", processedComedySeries);
    }

    private Object getRandomPromotedItem(){
        List<MovieDto> allPromotedMovies = movieService.findAllPromotedMovies();
        List<SeriesDto> allPromotedSeries = seriesService.findAllPromotedMovies();

        List<Object> promotedItems = new ArrayList<>();
        promotedItems.addAll(allPromotedMovies);
        promotedItems.addAll(allPromotedSeries);

        int size = promotedItems.size();
        if (size > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(size);
            return promotedItems.get(randomIndex);
        }else {
//            TO DO
            return null;
        }
    }

    private List<SeriesDto> getSeriesPromoBox(){
        return Stream.of("tt7817340", "tt6470478", "tt4655480", "tt6236572", "tt6664638")
                .flatMap(imdbId -> seriesService.getSeriesByImdbId(imdbId).stream())
                .collect(Collectors.toList());
    }

    private List<MovieDto> getMoviePromoBox(){
        return Stream.of("tt0993842", "tt4034228", "tt2304933", "tt6644200", "tt6146586")
                .flatMap(imdbId -> movieService.getMoviesByImdbId(imdbId).stream())
                .collect(Collectors.toList());
    }
}
