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
import org.springframework.http.MediaType;
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

import dc.codecademy.miniproject.models.CustomException;
import dc.codecademy.miniproject.models.ErrorResponse;
import dc.codecademy.miniproject.models.Pair;
import dc.codecademy.miniproject.models.PhotoMetadata;
import dc.codecademy.miniproject.services.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/api/v1/files")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Photos Operations", description = "API's for performing operations, like " +
        "uploading a photo, downloading a photo, deleting them ...")
public class FileOpsController {

    @Autowired
    private StorageService storageService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Upload a file", description = "Uploads a file to the server.", responses = {
            @ApiResponse(responseCode = "201", description = "Success", useReturnTypeSchema = true, content = @Content(schema = @Schema(implementation = PhotoMetadata.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "409", description = "Conflict when a file already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })

    public ResponseEntity<PhotoMetadata> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String fileName,
            Authentication auth) throws CustomException, IOException {

        if (storageService.fileAlreadyExists(fileName, auth)) {

            throw new CustomException(
                    "A file already exists with give name try updating or creating with different name",
                    HttpStatus.CONFLICT);
        }
        var result = storageService.saveFile(file, fileName, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }

    @GetMapping
    @Operation(summary = "Get all photos", description = "Gets all photos from the server.", responses = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true, content = @Content(schema = @Schema(implementation = PhotoMetadata.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<List<PhotoMetadata>> getAllPhotos(Authentication auth) {
        List<PhotoMetadata> result = storageService.getAllPhotoMetadata(auth);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete photo", description = "Deletes a photo from the server.", responses = {
            @ApiResponse(responseCode = "204", description = "Success", useReturnTypeSchema = true, content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Photo not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<Void> deletePhoto(@PathVariable long id, Authentication auth)
            throws IOException, CustomException {
        if (storageService.deleteFile(id, auth)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new CustomException("No file found to delete", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Download Photo", description = "Downloads a file from the server.", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = Resource.class), mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Photo not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
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

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Update Photo", description = "Updates a file on the server.", responses = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true, content = @Content(schema = @Schema(implementation = PhotoMetadata.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = Void.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleSQLExceptions(Exception exe) {
        log.error(exe.getMessage(), exe);
        if (CustomException.class.isAssignableFrom(exe.getClass())) {
            CustomException cExe = (CustomException) exe;
            return ResponseEntity.status(cExe.getStatusCode())
                    .body(new ErrorResponse(exe.getMessage()));
        } else if (SQLException.class.isAssignableFrom(exe.getClass())
                || DataIntegrityViolationException.class.isAssignableFrom(exe.getClass())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(exe.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(exe.getMessage()));
    }
}
