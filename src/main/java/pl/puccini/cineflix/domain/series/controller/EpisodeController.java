package pl.puccini.cineflix.domain.series.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.dto.episodeDto.EpisodeInfoDto;
import pl.puccini.cineflix.domain.series.service.EpisodeService;
import pl.puccini.cineflix.domain.series.service.SeriesService;
import pl.puccini.cineflix.domain.user.service.UserUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class EpisodeController {
    private final EpisodeService episodeService;
    private final UserUtils userUtils;

    public EpisodeController(EpisodeService episodeService, SeriesService seriesService, UserUtils userUtils) {
        this.episodeService = episodeService;
        this.userUtils = userUtils;
    }

    @GetMapping("/play-episode/{episodeId}")
    public String playEpisode(@PathVariable Long episodeId, Authentication authentication, Model model) {
        userUtils.addAvatarUrlToModel(authentication, model);
        EpisodeDto episodeDto = episodeService.getEpisodeById(episodeId);
        if (episodeDto == null) {
            return "error/not-found";
        }
        EpisodeInfoDto episodeInfo = episodeService.getEpisodeInfo(episodeId);

        String videoId = extractVideoId(episodeDto.getMediaUrl());
        String youTubeUrl = "https://www.youtube.com/embed/" + videoId;
        model.addAttribute("mediaUrl", youTubeUrl);

        model.addAttribute("serialTitle", episodeInfo.getSerialTitle());
        model.addAttribute("seasonNumber", episodeInfo.getSeasonNumber());
        model.addAttribute("episodeNumber", episodeInfo.getEpisodeNumber());
        return "episode-player";
    }

    private static String extractVideoId(String youtubeUrl) {
        Pattern pattern = Pattern.compile("https?://www\\.youtube\\.com/watch\\?v=([\\w-]+)");
        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("https?://youtu\\.be/([\\w-]+)");
        matcher = pattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }


}
