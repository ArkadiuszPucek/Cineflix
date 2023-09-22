package pl.puccini.viaplay.domain.series.episodes;

import org.springframework.stereotype.Service;

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
