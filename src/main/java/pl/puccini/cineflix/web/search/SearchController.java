package pl.puccini.cineflix.web.search;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.puccini.cineflix.domain.kids.KidsMovieService;
import pl.puccini.cineflix.domain.kids.KidsSeriesService;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {
    private final MovieFacade movieFacade;
    private final UserUtils userUtils;
    private final KidsMovieService kidsMovieService;
    private final KidsSeriesService kidsSeriesService;
    private final SeriesFacade seriesFacade;

    public SearchController(MovieFacade movieFacade, UserUtils userUtils, KidsMovieService kidsMovieService, KidsSeriesService kidsSeriesService, SeriesFacade seriesFacade) {
        this.movieFacade = movieFacade;
        this.userUtils = userUtils;
        this.kidsMovieService = kidsMovieService;
        this.kidsSeriesService = kidsSeriesService;
        this.seriesFacade = seriesFacade;
    }


    @GetMapping("/search")
    public String search(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "filter", defaultValue = "all") String filter,
            Model model, Authentication authentication) {

        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);


        if(query != null && !query.isEmpty()) {
            String loweredQuery = query.toLowerCase();

            List<MovieDto> movies = movieFacade.searchMovies(loweredQuery);
            List<SeriesDto> series = seriesFacade.searchSeries(loweredQuery);

            if ("movies".equals(filter)) {
                model.addAttribute("activeAttribute", movies);
            } else if ("series".equals(filter)) {
                model.addAttribute("activeAttribute", series);
            } else if ("kids".equals(filter)) {
                List<MovieDto> allKidsMovies = kidsMovieService.getAllKidsMovies(userId);
                List<SeriesDto> allKidsSeries = kidsSeriesService.getAllKidsSeries(userId);
                List<Object> mixedKidsContent = new ArrayList<>();
                mixedKidsContent.addAll(allKidsMovies);
                mixedKidsContent.addAll(allKidsSeries);
                Collections.shuffle(mixedKidsContent);
                model.addAttribute("activeAttribute", mixedKidsContent);
            } else {
                List<Object> mixedResults = new ArrayList<>();
                mixedResults.addAll(movies);
                mixedResults.addAll(series);
                Collections.shuffle(mixedResults);
                model.addAttribute("activeAttribute", mixedResults);
            }
        }
        model.addAttribute("searchQuery", query);
        model.addAttribute("activeFilter", filter);
        return "search";
    }


}
