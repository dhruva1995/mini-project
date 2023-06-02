package dc.codecademy.miniproject.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dc.codecademy.miniproject.models.Pair;
import dc.codecademy.miniproject.models.PhotoMetadata;
import dc.codecademy.miniproject.models.User;
import dc.codecademy.miniproject.repositories.PhotoMetadataRepository;
import jakarta.transaction.Transactional;

@Service
public class StorageService {

    @Value("${storage.path}")
    private String storagePath;

    @Autowired
    private PhotoMetadataRepository photoRepo;

    public boolean fileAlreadyExists(String fileName, Authentication auth) {
        return photoRepo.findByUserAndName(getUser(auth), fileName).isPresent();
    }

    @Transactional
    public PhotoMetadata saveFile(MultipartFile file, String fileName, Authentication auth) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            fileName = file.getOriginalFilename();
        }
        LocalDateTime now = LocalDateTime.now();
        PhotoMetadata metadata = photoRepo.save(new PhotoMetadata(getUser(auth), fileName, now, now));
        Files.write(getBasePath().resolve(metadata.getIdAsString()), file.getBytes());

        return metadata;
    }

    public List<PhotoMetadata> getAllPhotoMetadata(Authentication auth) {
        return photoRepo.findByUser(getUser(auth));
    }

    @Transactional
    public boolean deleteFile(long id, Authentication auth) throws IOException {
        var photoMetadata = this.getPhotoMetadata(id, auth);
        if (photoMetadata.isPresent()) {
            photoRepo.delete(photoMetadata.get());
            Files.deleteIfExists(getBasePath().resolve(photoMetadata.get().getIdAsString()));
            return true;
        } else {
            return false;
        }
    }

    public Optional<Pair<Resource, PhotoMetadata>> getFileContent(long id, Authentication auth) {
        // TODO start here!
        var optMetadata = getPhotoMetadata(id, auth);
        if (optMetadata.isPresent()) {
            PhotoMetadata photoMeta = optMetadata.get();
            Resource resource = new FileSystemResource(getBasePath().resolve(photoMeta.getIdAsString()));
            return Optional.of(new Pair<>(resource, photoMeta));
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<PhotoMetadata> updateFile(MultipartFile file, String fileName, Authentication auth, long id)
            throws IOException {
        var optPhotoMetadata = getPhotoMetadata(id, auth);
        if (optPhotoMetadata.isEmpty())
            return Optional.empty();
        if (fileName == null || fileName.isEmpty()) {
            fileName = file.getOriginalFilename();
        }
        PhotoMetadata oldMetadata = optPhotoMetadata.get();
        oldMetadata.setName(fileName);
        oldMetadata.setUpdateAt(LocalDateTime.now());
        oldMetadata = photoRepo.save(oldMetadata);
        Files.write(getBasePath().resolve(oldMetadata.getIdAsString()), file.getBytes());
        return Optional.of(oldMetadata);
    }

    private Optional<PhotoMetadata> getPhotoMetadata(long id, Authentication auth) {
        return photoRepo.findByUserAndId(getUser(auth), id);
    }

    private User getUser(Authentication auth) {
        String userId = ((org.springframework.security.oauth2.jwt.Jwt) auth.getPrincipal()).getClaimAsString("userId");
        return new User(Long.parseLong(userId));
    }

    private Path getBasePath() {
        return Paths.get(storagePath);
    }

}
