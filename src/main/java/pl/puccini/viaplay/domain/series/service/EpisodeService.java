package pl.puccini.viaplay.domain.series.service;

import org.springframework.stereotype.Service;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDto;
import pl.puccini.viaplay.domain.series.dto.episodeDto.EpisodeDtoMapper;
import pl.puccini.viaplay.domain.series.repository.EpisodeRepository;
import pl.puccini.viaplay.domain.series.model.Episode;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;

    public EpisodeService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    List<Episode> getAllEpisodes(){
        return episodeRepository.findAllBy();
    }

    public List<EpisodeDto> getEpisodesForSeason(Long seasonId) {
        // Przyjmuję, że EpisodeRepository ma metodę do pobierania epizodów dla danego sezonu
        List<Episode> episodes = episodeRepository.findBySeasonId(seasonId);

        // Następnie możesz przekształcić encje Episode na obiekty EpisodeDto za pomocą mappera
        List<EpisodeDto> episodeDtos = episodes.stream()
                .map(EpisodeDtoMapper::map) // Wykorzystaj swojego mappera
                .collect(Collectors.toList());

        return episodeDtos;
    }
}
