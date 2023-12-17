package pl.puccini.cineflix.domain.series.main.episode.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.puccini.cineflix.domain.MediaUtils;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFacade;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeInfoDto;
import pl.puccini.cineflix.domain.series.main.episode.service.EpisodeService;
import pl.puccini.cineflix.domain.UserUtils;

@Controller
public class EpisodeController {
    private final EpisodeFacade episodeFacade;
    private final MediaUtils mediaUtils;
    private final UserUtils userUtils;

    public EpisodeController(EpisodeFacade episodeFacade, MediaUtils mediaUtils, UserUtils userUtils) {
        this.episodeFacade = episodeFacade;
        this.mediaUtils = mediaUtils;
        this.userUtils = userUtils;
    }

    @GetMapping("/play-episode/{episodeId}")
    public String playEpisode(@PathVariable Long episodeId, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        Long userId = userUtils.getUserIdFromAuthentication(authentication);
        EpisodeDto episodeDto = episodeFacade.getEpisodeById(episodeId);
        if (episodeDto == null) {
            return "error/not-found";
        }
        EpisodeInfoDto episodeInfo = episodeFacade.getEpisodeInfo(episodeId).orElseThrow(
                () -> new RuntimeException("EpisodeInfo not found"));

        String youTubeUrl = mediaUtils.extractVideoId(episodeDto.getMediaUrl());

        EpisodeDto firstUnwatchedEpisode = episodeFacade.findFirstUnwatchedEpisode(episodeInfo.getImdbId(), userId);
        model.addAttribute("watchedEpisodes", firstUnwatchedEpisode);

        model.addAttribute("mediaUrl", youTubeUrl);
        model.addAttribute("serialTitle", episodeInfo.getSerialTitle());
        model.addAttribute("seasonNumber", episodeInfo.getSeasonNumber());
        model.addAttribute("episodeNumber", episodeInfo.getEpisodeNumber());
        return "episode-player";
    }



}
