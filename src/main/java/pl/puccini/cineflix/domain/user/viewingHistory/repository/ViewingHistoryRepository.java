package pl.puccini.cineflix.domain.user.viewingHistory.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.puccini.cineflix.domain.user.viewingHistory.model.ViewingHistory;

import java.util.List;

@Repository
public interface ViewingHistoryRepository extends CrudRepository<ViewingHistory, Long> {
    List<ViewingHistory> findByUserId(Long userId);

    void deleteUserById(Long userId);
}
