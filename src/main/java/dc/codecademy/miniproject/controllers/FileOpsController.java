package dc.codecademy.miniproject.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dc.codecademy.miniproject.models.PhotoMetadata;
import dc.codecademy.miniproject.services.StorageService;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/api/v1/files")
public class FileOpsController {

    @Autowired
    private StorageService storageService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String fileName,
            Authentication auth) {
        try {
            var result = storageService.saveFile(file, fileName, auth);
            return ResponseEntity.ok(result);
        } catch (Exception exe) {
            log.error("Error while saving a file", exe);
            return ResponseEntity.badRequest().body(exe.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPhotos(Authentication auth) {
        try {
            List<PhotoMetadata> result = storageService.getAllPhotoMetadata(auth);
            return new ResponseEntity<List<PhotoMetadata>>(result, HttpStatus.OK);
        } catch (Exception exe) {
            log.error("Error while fetching photos", exe);
            return ResponseEntity.badRequest().body(exe.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable long id, Authentication auth) throws IOException {
        if (storageService.deleteFile(id, auth)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> dowloadFileContent(@PathVariable long id, Authentication auth) {
        Optional<Resource> optResource = storageService.getFileContent(id, auth);
        if (optResource.isPresent()) {
            var file = optResource.get();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream").body(file);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFile(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String fileName,
            Authentication auth, @PathVariable long id) throws IOException {
        var result = storageService.updateFile(file, fileName, auth, id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
