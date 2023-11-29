package pl.puccini.cineflix.domain.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.puccini.cineflix.domain.user.model.UserList;

import java.util.List;

@Repository
public interface UserListRepository extends CrudRepository<UserList, Long> {

    List<UserList> findByUserId(Long userId);


    // Usunięcie filmu/serialu z listy użytkownika
    void deleteByUserIdAndImdbId(Long userId, String imdbId);
    void deleteByImdbId(String imdbId);
    void deleteUserById(Long userId);

    // Sprawdzenie czy dany film/serial jest już na liście użytkownika
    boolean existsByUserIdAndImdbId(Long userId, String imdbId);
}
