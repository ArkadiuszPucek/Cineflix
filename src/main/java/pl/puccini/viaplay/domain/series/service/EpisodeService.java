package pl.puccini.viaplay.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.series.repository.EpisodeRepository;
import pl.puccini.viaplay.domain.series.model.Episode;

import java.util.List;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;

    public EpisodeService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    List<Episode> getAllEpisodes(){
        return episodeRepository.findAllBy();
    }
}
