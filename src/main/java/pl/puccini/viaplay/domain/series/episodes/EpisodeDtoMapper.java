package pl.puccini.viaplay.domain.series.episodes;

public class EpisodeDtoMapper {
    public static EpisodeDto map(Episode episode){
        return new EpisodeDto(
                episode.getId(),
                episode.getEpisodeNumber(),
                episode.getEpisodeTitle(),
                episode.getUrlLink(),
                episode.getDurationMinutes()
        );
    }
}
