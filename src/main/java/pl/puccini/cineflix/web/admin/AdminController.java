package pl.puccini.cineflix.web.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.config.carousel.movies.service.MovieCarouselService;
import pl.puccini.cineflix.config.carousel.series.service.SeriesCarouselService;
import pl.puccini.cineflix.domain.exceptions.*;
import pl.puccini.cineflix.domain.genre.Genre;
import pl.puccini.cineflix.domain.genre.GenreService;
import pl.puccini.cineflix.domain.movie.MovieFacade;
import pl.puccini.cineflix.domain.movie.dto.MovieDto;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.movie.model.MoviesPromoBox;
import pl.puccini.cineflix.domain.movie.repository.MoviesPromoBoxRepository;
import pl.puccini.cineflix.domain.movie.service.MoviePromotionService;
import pl.puccini.cineflix.domain.movie.service.MovieService;
import pl.puccini.cineflix.domain.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.model.Episode;
import pl.puccini.cineflix.domain.series.model.Series;
import pl.puccini.cineflix.domain.series.model.SeriesPromoBox;
import pl.puccini.cineflix.domain.series.repository.SeriesPromoBoxRepository;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.series.service.SeriesPromotionService;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.model.User;
import pl.puccini.cineflix.domain.user.service.UserService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class AdminController {
    private final EpisodeService episodeService;
    private final GenreService genreService;
    private final UserService userService;
    private final UserUtils userUtils;
    private final SeriesPromoBoxRepository seriesPromoBoxRepository;
    private final MoviesPromoBoxRepository moviesPromoBoxRepository;
    private final SeriesCarouselService seriesCarouselService;
    private final MovieCarouselService movieCarouselService;
    private final MoviePromotionService moviePromotionService;
    private final SeriesPromotionService seriesPromotionService;
    private final MovieFacade movieFacade;
    private final SeriesFacade seriesFacade;

    public AdminController(EpisodeService episodeService, GenreService genreService, UserService userService, UserUtils userUtils, SeriesPromoBoxRepository seriesPromoBoxRepository, MoviesPromoBoxRepository moviesPromoBoxRepository, SeriesCarouselService seriesCarouselService, MovieCarouselService movieCarouselService, MoviePromotionService moviePromotionService, SeriesPromotionService seriesPromotionService, MovieFacade movieFacade, SeriesFacade seriesFacade) {
        this.episodeService = episodeService;
        this.genreService = genreService;
        this.userService = userService;
        this.userUtils = userUtils;
        this.seriesPromoBoxRepository = seriesPromoBoxRepository;
        this.moviesPromoBoxRepository = moviesPromoBoxRepository;
        this.seriesCarouselService = seriesCarouselService;
        this.movieCarouselService = movieCarouselService;
        this.moviePromotionService = moviePromotionService;
        this.seriesPromotionService = seriesPromotionService;
        this.movieFacade = movieFacade;
        this.seriesFacade = seriesFacade;
    }


    @GetMapping("/admin")
    public String getAdminPanel(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);

        return "admin/admin";
    }

    @PostMapping("/admin/add-movie-form")
    public String addMovieManual(MovieDto movie, RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        if (movieFacade.doesMovieExists(movie.getImdbId())) {
            redirectAttributes.addFlashAttribute("error", "A film with the given IMDb id exists on the website!");
            return "redirect:/add-movie-form";
        }
        movieFacade.addMovieManual(movie);
        String normalizedTitle = movie.getTitle().toLowerCase().replace(" ", "-");

        return "redirect:/movies/" + normalizedTitle;
    }

    @GetMapping("/admin/add-movie-form")
    public String showAddMovieManualForm(Authentication authentication ,Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-form";
    }

    @PostMapping("/admin/add-movie-api")
    public String addMovieByApi(MovieDto movie, RedirectAttributes redirectAttributes) {
        try {
            Movie movieFromApi = movieFacade.addMovieIfNotExist(movie);
            String normalizedTitle = movieFromApi.getTitle().toLowerCase().replace(" ", "-");
            return "redirect:/movies/" + normalizedTitle;
        } catch (IncorrectTypeException | MovieAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/add-movie-api";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "An error occured: " + e.getMessage());
            return "redirect:/admin/add-movie-api";
        }
    }

    @GetMapping("/admin/add-movie-api")
    public String showAddMovieApiForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        MovieDto movie = new MovieDto();
        model.addAttribute("movie", movie);

        return "admin/movies/add-movie-api";
    }


    @GetMapping("/admin/edit-movie/{imdbId}")
    public String showEditMovieForm(@PathVariable String imdbId, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        MovieDto movieByImdbId = movieFacade.getMovieDtoByImdbId(imdbId);

        model.addAttribute("movie", movieByImdbId);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/movies/edit-movie-form";
    }

    @PostMapping("/admin/update-movie")
    public String updateMovie(@ModelAttribute("movie") MovieDto movieDto, RedirectAttributes redirectAttributes) {
        try {
            boolean updateResult = movieFacade.updateMovie(movieDto);

            if (updateResult) {
                redirectAttributes.addFlashAttribute("success", "The video has been successfully updated.");
                return "redirect:/admin/manage-movies";
            } else {
                redirectAttributes.addFlashAttribute("error", "Movie not found");
                return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the video: " + e.getMessage());
            return "redirect:/admin/edit-movie/" + movieDto.getImdbId();
        }
    }

    @GetMapping("/admin/delete-movie/{imdbId}")
    public String deleteMovie(@PathVariable String imdbId,
                              RedirectAttributes redirectAttributes,
                              @RequestParam String action,
                              Authentication authentication,
                              Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        if ("deleteMovie".equals(action)) {
            boolean deleted = movieFacade.deleteMovie(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "The video has been successfully removed.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Movie not found");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the video.");
        }
        return "redirect:/admin/manage-movies";
    }

    @GetMapping("/admin/manage-movies")
    public String showManageMovieForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        List<MovieDto> allMoviesInService = movieFacade.findAllMovies(userId);
        model.addAttribute("allMoviesInService", allMoviesInService);

        return "admin/movies/manage-movies";
    }

    @GetMapping("/admin/manage-movies-promo-box")
    public String moviesPromoBoxForm(Model model, Authentication authentication){
        userUtils.addAvatarUrlToModel(authentication, model);
        MoviesPromoBox currentPromoBox = moviesPromoBoxRepository.findTopByOrderByIdDesc();
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        List<MovieDto> promoBoxImdbIds = moviePromotionService.getMoviePromoBox(userId);
        model.addAttribute("currentPromoBoxImdbIds", promoBoxImdbIds);

        model.addAttribute("currentPromoBox", currentPromoBox);

        return "/admin/movies/movies-promo-box-form";
    }

    @PostMapping("/admin/manage-movies-promo-box/save")
    public String saveMoviesPromoBox(
            @RequestParam String title,
            @RequestParam String imdbId1,
            @RequestParam String imdbId2,
            @RequestParam String imdbId3,
            @RequestParam String imdbId4,
            @RequestParam String imdbId5,
            RedirectAttributes redirectAttributes) {
        try {
            moviePromotionService.updateMoviePromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        }catch (MovieNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/admin/manage-movies-promo-box";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/manage-movies-carousels")
    public String manageMoviesCarouselForm(Model model){
        List<Genre> genres = genreService.getGenresWithMinimumMovies(2);
        List<String> activeGenres = movieCarouselService.getSelectedGenres();

        Map<String, Boolean> genresWithStatus = genres.stream()
                .collect(Collectors.toMap(Genre::getGenreType, genre -> activeGenres.contains(genre.getGenreType())));

        model.addAttribute("genresWithStatus", genresWithStatus);

        return "admin/movies/manage-movies-carousels-form";
    }

    @PostMapping("/admin/manage-movies-carousels/save")
    public String saveMoviesCarousels(@RequestParam(required = false) List<String> selectedGenres, RedirectAttributes redirectAttributes){
        if (selectedGenres == null) {
            selectedGenres = new ArrayList<>();
        }
        movieCarouselService.saveSelectedGenres(selectedGenres);

        redirectAttributes.addFlashAttribute("message", "Carousels settings updated successfully.");
        return "redirect:/admin/manage-movies-carousels";
    }


//SERIES


    @PostMapping("/admin/add-series-form")
    public String addSeriesManual(@ModelAttribute SeriesDto series, RedirectAttributes redirectAttributes, HttpSession session) {
        if (seriesFacade.doesSeriesExists(series.getImdbId())) {
            redirectAttributes.addFlashAttribute("message", "A film with the given IMDb id exists on the website!");
            return "redirect:/admin/add-series-form";
        }

        try {
            seriesFacade.addSeriesManual(series);
            session.setAttribute("seasonsCount", series.getSeasonsCount());
            session.setAttribute("title", series.getTitle());
            redirectAttributes.addFlashAttribute("seasonsCount", series.getSeasonsCount());
            redirectAttributes.addFlashAttribute("message", "The series has been added.");
            return "redirect:/admin/add-episode/"+series.getImdbId()+"/1/1";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "Error adding series: " + e.getMessage());
            return "redirect:/admin/add-series-form";
        }
    }
    @GetMapping("/admin/add-series-form")
    public String showAddSeriesManualForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);

        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        SeriesDto series = new SeriesDto();
        model.addAttribute("series", series);

        return "admin/series/add-series-form";
    }

    @GetMapping("/admin/add-episode/{seriesId}/{seasonNumber}/{episodeNumber}")
    public String showAddEpisodeForm(@PathVariable String seriesId,
                                     @PathVariable int seasonNumber,
                                     @PathVariable int episodeNumber,
                                     Authentication authentication,
                                     Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        EpisodeDto episodeDto = new EpisodeDto();
        model.addAttribute("episode", episodeDto);
        model.addAttribute("seriesId", seriesId);
        model.addAttribute("seasonNumber", seasonNumber);
        model.addAttribute("episodeNumber", episodeNumber);
        return "admin/series/add-episode-form";
    }

    @PostMapping("/admin/add-episode/{seriesId}/{seasonNumber}/{episodeNumber}")
    public String addEpisode(@ModelAttribute EpisodeDto episodeDto,
                             @PathVariable String seriesId,
                             @PathVariable int seasonNumber,
                             @PathVariable int episodeNumber,
                             HttpSession session,
                             @RequestParam String action,
                             RedirectAttributes redirectAttributes) {
        try {
            Integer seasonsCount = (Integer) session.getAttribute("seasonsCount");
            String seriesTitle = (String) session.getAttribute("title");

            if (seasonsCount == null) {
                redirectAttributes.addFlashAttribute("message", "An error occurred while adding the number of seasons.");
                return "redirect:/admin";
            }

            episodeDto.setEpisodeNumber(episodeNumber);
            String redirectUrl = episodeService.processEpisodeAddition(episodeDto, seriesId, seasonNumber, episodeNumber, seasonsCount, action, seriesTitle);
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occured: " + e.getMessage());
            return "redirect:/admin/add-episode/" + seriesId + "/" + seasonNumber + "/" + episodeNumber;
        }
    }


    @PostMapping("/admin/add-series-api")
    public String addSeriesByApi(SeriesDto series, RedirectAttributes redirectAttributes) {
        try {
            Series seriesFromApi = seriesFacade.addSeriesByApiIfNotExist(series);
            String normalizedTitle = seriesFacade.formatSeriesTitle(seriesFromApi.getTitle());
            return "redirect:/series/" + normalizedTitle + "/sezon-1";
        }catch (IncorrectTypeException | SeriesAlreadyExistsException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/add-series-api";
        } catch (IOException | InterruptedException e) {
            redirectAttributes.addFlashAttribute("error", "An error occured: " + e.getMessage());
            return "redirect:/admin/add-series-api";
        }
    }

    @GetMapping("/admin/add-series-api")
    public String showAddSeriesApiForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        SeriesDto seriesDto = new SeriesDto();
        model.addAttribute("series", seriesDto);

        return "admin/series/add-series-api";
    }

    @GetMapping("/admin/manage-series")
    public String showManageSeriesForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        List<SeriesDto> allSeriesInService = seriesFacade.findAllSeries(userId);
        model.addAttribute("allSeriesInService", allSeriesInService);

        return "admin/series/manage-series";
    }

    @GetMapping("/admin/edit-series/{imdbId}")
    public String showEditSeriesForm(@PathVariable String imdbId, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        SeriesDto seriesByImdbId = seriesFacade.getSeriesDtoByImdbId(imdbId);

        model.addAttribute("series", seriesByImdbId);
        List<Genre> allGenres = genreService.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/series/edit-series-form";
    }

    @PostMapping("/admin/update-series")
    public String updateSeries(@ModelAttribute("series") SeriesDto seriesDto, RedirectAttributes redirectAttributes) {
        try {
            seriesFacade.updateSeries(seriesDto);
            redirectAttributes.addFlashAttribute("success", "The series has been successfully updated.");
            return "redirect:/admin/manage-series";
        } catch (SeriesNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/manage-series";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the series " + e.getMessage());
            return "redirect:/admin/edit-series/" + seriesDto.getImdbId();
        }
    }

    @GetMapping("/admin/delete-series/{imdbId}")
    public String deleteSeries(@PathVariable String imdbId,
                               RedirectAttributes redirectAttributes,
                               @RequestParam String action,
                               Authentication authentication,
                               Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);

        if ("deleteSeries".equals(action)) {
            boolean deleted = seriesFacade.deleteSeries(imdbId);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "The series has been successfully removed.");
            } else {
                redirectAttributes.addFlashAttribute("error", "The series was not found.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the series: ");
        }
        return "redirect:/admin/manage-series";
    }

    @GetMapping("/admin/edit-series-seasons/{imdbId}")
    public String showManageSeasonsForm(@PathVariable String imdbId, Model model, HttpSession session, Authentication authentication) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Series series = seriesFacade.getSeriesByImdbId(imdbId);
        session.setAttribute("imdbId", imdbId);

        if (series == null){
            return "redirect:/admin/manage-series";
        }

        model.addAttribute("series", series);
        model.addAttribute("seasons", series.getSeasons());
        return "admin/series/manage-seasons";
    }

    @GetMapping("/admin/edit-episode/{episodeId}")
    public String showEpisodeEditForm(@PathVariable Long episodeId, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        EpisodeDto episodeById = episodeService.getEpisodeById(episodeId);
        model.addAttribute("episode", episodeById);
        return "admin/series/edit-episode-form";
    }

    @PostMapping("/admin/update-episode")
    public String updateSeries(@ModelAttribute("episode") EpisodeDto episodeDto,
                               RedirectAttributes redirectAttributes) {
        try {
            Episode updatedEpisode = episodeService.updateEpisode(episodeDto);
            String seriesImdbId = updatedEpisode.getSeason().getSeries().getImdbId();
            redirectAttributes.addFlashAttribute("message", "The episode has been successfully updated.");
            return "redirect:/admin/edit-series-seasons/" + seriesImdbId;
        } catch (EpisodeNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred while editing the episode " + e.getMessage());
            return "redirect:/admin/manage-series";
        }
    }

    @GetMapping("/admin/delete-episode/{episodeId}")
    public String deleteEpisode(@PathVariable Long episodeId,
                               RedirectAttributes redirectAttributes,
                               @RequestParam String action,
                                Authentication authentication,
                                Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);

        try{
            if ("deleteEpisode".equals(action)){
                Episode episodeToDelete = episodeService.deleteEpisodeById(episodeId);
                String imdbId = episodeToDelete.getSeason().getSeries().getImdbId();
                redirectAttributes.addFlashAttribute("message", "The episode was successfully deleted.");
                return "redirect:/admin/edit-series-seasons/" + imdbId;
            }else {
                redirectAttributes.addFlashAttribute("message", "The episode has not been removed.");
                return "redirect:/admin/manage-series";
            }
        } catch (EpisodeNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred while editing the episode " + e.getMessage());
            return "redirect:/admin/manage-series";
        }
    }
    @GetMapping("/admin/manage-series-promo-box")
    public String seriesPromoBoxForm(Model model, Authentication authentication){
        userUtils.addAvatarUrlToModel(authentication, model);
        SeriesPromoBox currentPromoBox = seriesPromoBoxRepository.findTopByOrderByIdDesc();
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        List<SeriesDto> promoBoxImdbIds = seriesPromotionService.getSeriesPromoBox(userId);
        model.addAttribute("currentPromoBoxImdbIds", promoBoxImdbIds);

        model.addAttribute("currentPromoBox", currentPromoBox);

        return "/admin/series/series-promo-box-form";
    }

    @PostMapping("/admin/manage-series-promo-box/save")
    public String saveSeriesPromoBox(
            @RequestParam String title,
            @RequestParam String imdbId1,
            @RequestParam String imdbId2,
            @RequestParam String imdbId3,
            @RequestParam String imdbId4,
            @RequestParam String imdbId5,
            RedirectAttributes redirectAttributes
    ) {
        try {
            seriesPromotionService.updateSeriesPromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        }catch (SeriesNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/admin/manage-series-promo-box";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/manage-series-carousels")
    public String manageSeriesCarouselForm(Model model){
        List<Genre> genres = genreService.getGenresWithMinimumSeries(1);
        List<String> activeGenres = seriesCarouselService.getSelectedGenres();

        Map<String, Boolean> genresWithStatus = genres.stream()
                .collect(Collectors.toMap(Genre::getGenreType, genre -> activeGenres.contains(genre.getGenreType())));

        model.addAttribute("genresWithStatus", genresWithStatus);

        return "admin/series/manage-series-carousels-form";
    }

    @PostMapping("/admin/manage-series-carousels/save")
    public String saveSeriesCarousels(@RequestParam(required = false) List<String> selectedGenres, RedirectAttributes redirectAttributes){
        if (selectedGenres == null) {
            selectedGenres = new ArrayList<>();
        }
        seriesCarouselService.saveSelectedGenres(selectedGenres);
        redirectAttributes.addFlashAttribute("message", "Carousels settings updated successfully.");
        return "redirect:/admin/manage-series-carousels";
    }



//USERS

    @GetMapping("/master/manage-users")
    public String showManageUsersForm(Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        List<User> allUsersInService = userService.getAllUsersInService();
        model.addAttribute("users", allUsersInService);


        return "admin/users/manage-users";
    }

    @PostMapping("/master/change-role")
    public String changeUserRole(@RequestParam Long user, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        try {
            userService.changeUserRole(user, newRole);
            redirectAttributes.addFlashAttribute("message", "The user role has been changed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occured: " + e.getMessage());
        }
        return "redirect:/master/manage-users";
    }

    @GetMapping("/master/delete-user/{userId}")
    public String deleteUser(@PathVariable Long userId,
                              RedirectAttributes redirectAttributes,
                              @RequestParam String action,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             Authentication authentication,
                             Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        if ("deleteUser".equals(action)) {
            userUtils.deleteUser(request, response, redirectAttributes, userId);
        } else {
            redirectAttributes.addFlashAttribute("message", "An error occurred while deleting the user.");
        }
        return "redirect:/master/manage-users";
    }


}
