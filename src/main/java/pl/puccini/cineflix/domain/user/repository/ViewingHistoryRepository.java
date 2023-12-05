package pl.puccini.cineflix.domain.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.puccini.cineflix.domain.user.model.ViewingHistory;

import java.util.List;

@Repository
public interface ViewingHistoryRepository extends CrudRepository<ViewingHistory, Long> {
    List<ViewingHistory> findByUserIdOrderByViewedOnDesc(Long userId);

    List<ViewingHistory> findByUserId(Long userId);

    void deleteUserById(Long userId);
}
