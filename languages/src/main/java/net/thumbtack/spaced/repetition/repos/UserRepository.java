package net.thumbtack.spaced.repetition.repos;

import net.thumbtack.spaced.repetition.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsernameIgnoreCase(String username);

    User findByUsernameOrEmailIgnoreCase(String username, String email);

    Page<User> findAllByUsernameStartsWithIgnoreCase(Pageable pageable, String filter);

}
