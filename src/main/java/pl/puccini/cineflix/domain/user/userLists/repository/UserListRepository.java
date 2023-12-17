package pl.puccini.cineflix.domain.user.userLists.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.puccini.cineflix.domain.user.userLists.model.UserList;

import java.util.List;

@Repository
public interface UserListRepository extends CrudRepository<UserList, Long> {

    List<UserList> findByUserId(Long userId);
    void deleteByUserIdAndImdbId(Long userId, String imdbId);
    void deleteByImdbId(String imdbId);
    void deleteUserById(Long userId);

    boolean existsByUserIdAndImdbId(Long userId, String imdbId);
}
