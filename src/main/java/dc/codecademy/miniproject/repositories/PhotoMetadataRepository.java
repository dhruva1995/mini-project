package dc.codecademy.miniproject.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dc.codecademy.miniproject.models.PhotoMetadata;
import dc.codecademy.miniproject.models.User;

public interface PhotoMetadataRepository extends JpaRepository<PhotoMetadata, Long> {

    List<PhotoMetadata> findByUser(User user);

    Optional<PhotoMetadata> findByUserAndId(User user, long id);

}
