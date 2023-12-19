package pl.puccini.cineflix.web.admin.seriesManagement;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.puccini.cineflix.domain.exceptions.EpisodeNotFoundException;
import pl.puccini.cineflix.domain.exceptions.IncorrectTypeException;
import pl.puccini.cineflix.domain.exceptions.SeriesAlreadyExistsException;
import pl.puccini.cineflix.domain.exceptions.SeriesNotFoundException;
import pl.puccini.cineflix.domain.genre.model.Genre;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SeriesAdminController {

    private final SeriesManagementFacade seriesManagementFacade;

    public SeriesAdminController(SeriesManagementFacade seriesManagementFacade) {
        this.seriesManagementFacade = seriesManagementFacade;
    }


    @PostMapping("/admin/add-series-form")
    public String addSeriesManual(@ModelAttribute SeriesDto series, RedirectAttributes redirectAttributes, HttpSession session) {
        if (seriesManagementFacade.doesSeriesExists(series.getImdbId())) {
            redirectAttributes.addFlashAttribute("message", "A film with the given IMDb id exists on the website!");
            return "redirect:/admin/add-series-form";
        }

        try {
            seriesManagementFacade.addSeriesManual(series);
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        List<Genre> allGenres = seriesManagementFacade.getAllGenres();
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
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
            String redirectUrl = seriesManagementFacade.processEpisodeAddition(episodeDto, seriesId, seasonNumber, episodeNumber, seasonsCount, action, seriesTitle);
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occured: " + e.getMessage());
            return "redirect:/admin/add-episode/" + seriesId + "/" + seasonNumber + "/" + episodeNumber;
        }
    }


    @PostMapping("/admin/add-series-api")
    public String addSeriesByApi(SeriesDto series, RedirectAttributes redirectAttributes) {
        try {
            Series seriesFromApi = seriesManagementFacade.addSeriesByApiIfNotExist(series);
            String normalizedTitle = seriesManagementFacade.formatSeriesTitle(seriesFromApi.getTitle());
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        SeriesDto seriesDto = new SeriesDto();
        model.addAttribute("series", seriesDto);

        return "admin/series/add-series-api";
    }

    @GetMapping("/admin/manage-series")
    public String showManageSeriesForm(Authentication authentication, Model model) {
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        Long userId = seriesManagementFacade.getUserIdFromAuthentication(authentication);
        List<SeriesDto> allSeriesInService = seriesManagementFacade.findAllSeries(userId);
        model.addAttribute("allSeriesInService", allSeriesInService);

        return "admin/series/manage-series";
    }

    @GetMapping("/admin/edit-series/{imdbId}")
    public String showEditSeriesForm(@PathVariable String imdbId, Authentication authentication, Model model) {
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        SeriesDto seriesByImdbId = seriesManagementFacade.getSeriesDtoByImdbId(imdbId);

        model.addAttribute("series", seriesByImdbId);
        List<Genre> allGenres = seriesManagementFacade.getAllGenres();
        model.addAttribute("genres", allGenres);
        List<Integer> ageLimits = Arrays.asList(3, 7, 12, 16, 18);
        model.addAttribute("ageLimits", ageLimits);

        return "admin/series/edit-series-form";
    }

    @PostMapping("/admin/update-series")
    public String updateSeries(@ModelAttribute("series") SeriesDto seriesDto, RedirectAttributes redirectAttributes) {
        try {
            seriesManagementFacade.updateSeries(seriesDto);
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);

        if ("deleteSeries".equals(action)) {
            boolean deleted = seriesManagementFacade.deleteSeries(imdbId);
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        Series series = seriesManagementFacade.getSeriesByImdbId(imdbId);
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        EpisodeDto episodeById = seriesManagementFacade.getEpisodeById(episodeId);
        model.addAttribute("episode", episodeById);
        return "admin/series/edit-episode-form";
    }

    @PostMapping("/admin/update-episode")
    public String updateSeries(@ModelAttribute("episode") EpisodeDto episodeDto,
                               RedirectAttributes redirectAttributes) {
        try {
            Episode updatedEpisode = seriesManagementFacade.updateEpisode(episodeDto);
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);

        try{
            if ("deleteEpisode".equals(action)){
                Episode episodeToDelete = seriesManagementFacade.deleteEpisodeById(episodeId);
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
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        Long userId = seriesManagementFacade.getUserIdFromAuthentication(authentication);

        model.addAttribute("currentPromoBoxImdbIds", seriesManagementFacade.getSeriesPromoBox(userId));
        model.addAttribute("seriesPromoBoxTitle", seriesManagementFacade.getSeriesPromoBoxTitle());

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
            seriesManagementFacade.updateSeriesPromoBox(title, imdbId1, imdbId2, imdbId3, imdbId4, imdbId5);
        }catch (SeriesNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/admin/manage-series-promo-box";
        }
        return "redirect:/";
    }

    @GetMapping("/admin/manage-series-carousels")
    public String manageSeriesCarouselForm(Model model, Authentication authentication){
        seriesManagementFacade.addAvatarUrlToModel(authentication, model);
        List<Genre> genres = seriesManagementFacade.getGenresWithMinimumSeries(1);
        List<String> activeGenres = seriesManagementFacade.getSelectedGenresForSeries();

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
        seriesManagementFacade.saveSelectedGenresForSeries(selectedGenres);
        redirectAttributes.addFlashAttribute("message", "Carousels settings updated successfully.");
        return "redirect:/admin/manage-series-carousels";
    }
}
