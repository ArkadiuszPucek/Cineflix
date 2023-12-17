package pl.puccini.cineflix.domain.user.userLists.service;

import org.springframework.stereotype.Service;
import pl.puccini.cineflix.domain.series.main.series.repository.SeriesRepository;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDto;
import pl.puccini.cineflix.domain.series.main.series.seriesDto.SeriesDtoMapper;
import pl.puccini.cineflix.domain.user.userLists.model.UserList;
import pl.puccini.cineflix.domain.user.userLists.repository.UserListRepository;
import pl.puccini.cineflix.domain.user.userRatings.service.UserSeriesRatingService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSeriesListService {
    private final SeriesRepository seriesRepository;
    private final UserListRepository userListRepository;
    private final UserSeriesRatingService userSeriesRatingService;

    public UserSeriesListService(SeriesRepository seriesRepository, UserListRepository userListRepository, UserSeriesRatingService userSeriesRatingService) {
        this.seriesRepository = seriesRepository;
        this.userListRepository = userListRepository;
        this.userSeriesRatingService = userSeriesRatingService;
    }

    public List<SeriesDto> getUserSeries(Long userId) {
        List<UserList> userLists = userListRepository.findByUserId(userId);
        List<SeriesDto> seriesDtos = new ArrayList<>();

        for (UserList userList : userLists) {
            seriesRepository.findSeriesByImdbId(userList.getImdbId())
                    .ifPresent(series -> {
                        SeriesDto mappedSeries = SeriesDtoMapper.map(series);
                        mappedSeries.setOnUserList(userListRepository.existsByUserIdAndImdbId(userId, mappedSeries.getImdbId()));
                        mappedSeries.setUserRating(userSeriesRatingService.getCurrentUserRatingForSeries(mappedSeries.getImdbId(), userId).orElse(null));
                        seriesDtos.add(mappedSeries);
                    });
        }
        return seriesDtos;
    }
}
