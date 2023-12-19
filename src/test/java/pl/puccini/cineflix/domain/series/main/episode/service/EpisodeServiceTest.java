package pl.puccini.cineflix.domain.series.main.episode.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puccini.cineflix.domain.series.main.episode.EpisodeFactory;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDto;
import pl.puccini.cineflix.domain.series.main.episode.episodeDto.EpisodeDtoMapper;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.series.main.episode.repository.EpisodeRepository;
import pl.puccini.cineflix.domain.series.main.season.model.Season;
import pl.puccini.cineflix.domain.series.main.season.service.SeasonService;
import pl.puccini.cineflix.domain.series.main.series.SeriesFacade;
import pl.puccini.cineflix.domain.series.main.series.model.Series;
import pl.puccini.cineflix.domain.user.viewingHistory.ViewingHistoryFacade;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EpisodeServiceTest {

    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private SeasonService seasonService;

    @Mock
    private ViewingHistoryFacade viewingHistoryFacade;

    @Mock
    private SeriesFacade seriesFacade;

    @Mock
    private EpisodeFactory episodeFactory;

    @InjectMocks
    private EpisodeService episodeService;

    @Test
    void addEpisodeSuccessfully() {
        EpisodeDto episodeDto = new EpisodeDto();
        String seriesId = "series1";
        int seasonNumber = 1;
        Season season = new Season();
        Episode episode = new Episode();

        when(seasonService.findOrCreateSeason(seriesId, seasonNumber)).thenReturn(season);
        when(episodeFactory.createEpisode(episodeDto, season)).thenReturn(episode);
        when(episodeRepository.save(episode)).thenReturn(episode);

        Episode savedEpisode = episodeService.addEpisode(episodeDto, seriesId, seasonNumber);

        verify(episodeFactory).createEpisode(episodeDto, season);
        verify(episodeRepository).save(episode);
        assertEquals(episode, savedEpisode);
    }

    @Test
    void getEpisodeByIdReturnsEpisodeDto() {
        Long episodeId = 1L;
        Episode episode = createMockEpisode();

        when(episodeRepository.findById(episodeId)).thenReturn(Optional.of(episode));

        EpisodeDto result = episodeService.getEpisodeById(episodeId);

        assertNotNull(result);
        assertEquals(episode.getEpisodeTitle(), result.getEpisodeTitle());
        assertEquals(episode.getMediaUrl(), result.getMediaUrl());
    }

    private Episode createMockEpisode() {
        Episode episode = new Episode();
        Season season = new Season();
        Series series = new Series();

        series.setTitle("Test Series");
        series.setImdbId("tt1234567");

        season.setSeries(series);
        season.setSeasonNumber(1);

        episode.setSeason(season);
        episode.setEpisodeTitle("Test Episode");
        episode.setMediaUrl("http://test.com");

        return episode;
    }

}
