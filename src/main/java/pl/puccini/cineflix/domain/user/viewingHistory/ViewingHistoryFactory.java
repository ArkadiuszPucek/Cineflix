package pl.puccini.cineflix.domain.user.viewingHistory;

import org.springframework.stereotype.Component;
import pl.puccini.cineflix.domain.movie.model.Movie;
import pl.puccini.cineflix.domain.series.main.episode.model.Episode;
import pl.puccini.cineflix.domain.user.userDetails.model.User;
import pl.puccini.cineflix.domain.user.viewingHistory.model.ViewingHistory;

import java.time.LocalDateTime;

@Component
public class ViewingHistoryFactory {
    public ViewingHistory createForMovie(User user, Movie movie) {
        ViewingHistory history = new ViewingHistory();
        history.setUser(user);
        history.setMovie(movie);
        history.setViewedOn(LocalDateTime.now());
        return history;
    }

    public ViewingHistory createForEpisode(User user, Episode episode) {
        ViewingHistory history = new ViewingHistory();
        history.setUser(user);
        history.setEpisode(episode);
        history.setViewedOn(LocalDateTime.now());
        return history;
    }
}
