package com.EnglishApp.learning_service.controller;

import com.EnglishApp.learning_service.storage.StorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
@RequestMapping("/api/learning/media")
public class LocalMediaController {
    private final Path storageRoot;

    public LocalMediaController(StorageProperties storageProperties) {
        this.storageRoot = Paths.get(storageProperties.getLocalRoot())
                .toAbsolutePath()
                .normalize();
    }

    @GetMapping("/{*objectKey}")
    public ResponseEntity<Resource> getMedia(HttpServletRequest request) {
        String objectKey = request.getRequestURI()
                .substring(request.getRequestURI().indexOf("/api/learning/media/") + "/api/learning/media/".length());
        Path mediaPath = storageRoot.resolve(objectKey).normalize();
        if (!mediaPath.startsWith(storageRoot) || !Files.isRegularFile(mediaPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found");
        }

        try {
            String contentType = Files.probeContentType(mediaPath);
            MediaType mediaType = contentType == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(contentType);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(new FileSystemResource(mediaPath));
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not read media",
                    exception
            );
        }
    }
}
