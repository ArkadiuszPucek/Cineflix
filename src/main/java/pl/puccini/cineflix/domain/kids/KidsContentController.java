package pl.puccini.cineflix.domain.kids;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/kids")
public class KidsContentController {
    private final KidsContentService kidsContentService;
    private final UserUtils userUtils;

    public KidsContentController(KidsContentService kidsContentService, UserUtils userUtils) {
        this.kidsContentService = kidsContentService;
        this.userUtils = userUtils;
    }

    @GetMapping()
    public String search(
            @RequestParam(name = "filter", defaultValue = "all") String filter,
            Authentication authentication,
            Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);

        List<MovieDto> allKidsMovies = kidsContentService.getAllKidsMovies(userId);
        List<SeriesDto> allKidsSeries = kidsContentService.getAllKidsSeries(userId);


        if ("movies".equals(filter)) {
            model.addAttribute("activeAttribute", allKidsMovies);
        } else if ("series".equals(filter)) {
            model.addAttribute("activeAttribute", allKidsSeries);
        } else {
            List<Object> mixedResults = new ArrayList<>();
            mixedResults.addAll(allKidsMovies);
            mixedResults.addAll(allKidsSeries);
            Collections.shuffle(mixedResults);
            model.addAttribute("activeAttribute", mixedResults);
        }
        model.addAttribute("activeFilter",filter);
        return"kids";
    }
}

