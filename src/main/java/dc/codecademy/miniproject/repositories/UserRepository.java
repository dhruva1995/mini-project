package dc.codecademy.miniproject.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dc.codecademy.miniproject.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
