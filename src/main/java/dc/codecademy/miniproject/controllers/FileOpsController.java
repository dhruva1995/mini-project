package dc.codecademy.miniproject.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dc.codecademy.miniproject.models.ErrorResponse;
import dc.codecademy.miniproject.models.Pair;
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
            Authentication auth) throws CustomException, IOException {

        if (storageService.fileAlreadyExists(fileName, auth)) {

            throw new CustomException(
                    "A file already exists with give name try updating or creating with different name",
                    HttpStatus.CONFLICT);
        }
        var result = storageService.saveFile(file, fileName, auth);
        return ResponseEntity.ok(result);

    }

    @GetMapping
    public ResponseEntity<List<PhotoMetadata>> getAllPhotos(Authentication auth) {
        List<PhotoMetadata> result = storageService.getAllPhotoMetadata(auth);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable long id, Authentication auth)
            throws IOException, CustomException {
        if (storageService.deleteFile(id, auth)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new CustomException("No file found to delete", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> dowloadFileContent(@PathVariable long id, Authentication auth)
            throws CustomException {

        Optional<Pair<Resource, PhotoMetadata>> optResource = storageService.getFileContent(id, auth);
        if (optResource.isPresent()) {
            var file = optResource.get().first();
            var fileName = optResource.get().second().getName();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(file);
        }
        throw new CustomException("File not found", HttpStatus.NOT_FOUND);

    }

    @PutMapping("/{id}")
    public ResponseEntity<PhotoMetadata> updateFile(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String fileName,
            Authentication auth, @PathVariable long id) throws IOException, CustomException {
        var result = storageService.updateFile(file, fileName, auth, id);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            throw new CustomException("No file found to update", HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptions(CustomException exe) {
        return ResponseEntity.status(exe.getStatusCode())
                .body(new ErrorResponse(exe.getMessage()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLExceptions(SQLException exe) {
        log.error(exe.getMessage(), exe);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(exe.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleSQLExceptions(DataIntegrityViolationException exe) {
        log.error(exe.getMessage(), exe);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(exe.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleSQLExceptions(Exception exe) {
        log.error(exe.getMessage(), exe);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(exe.getMessage()));
    }
}
