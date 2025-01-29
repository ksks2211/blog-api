package org.example.postapi.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.common.dto.ApiResponse;
import org.example.postapi.image.exception.InvalidFileTypeException;
import org.example.postapi.security.AuthUser;
import org.example.postapi.security.resolver.CurrentUserUUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.example.postapi.common.util.FileUtils.*;
import static org.example.postapi.image.ImageConstants.IMAGE_CACHE_NAME;
import static org.example.postapi.image.ImageConstants.IMAGE_URL_PREFIX;

/**
 * @author rival
 * @since 2025-01-29
 */

@RestController
@RequestMapping(IMAGE_URL_PREFIX)
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    // uploadImage


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadImage(@AuthenticationPrincipal AuthUser user, final MultipartFile file) {
        if(!isImageContentType(file)){
            throw new InvalidFileTypeException("File type not supported");
        }
        String filename = IMAGE_CACHE_NAME+"/"+ generateRandomFilename(user.getId().toString()) + getExtensionFromMimeType(file.getContentType());
        ImageDto imageDto = imageService.uploadImage(file, filename, user.getId());
        var body = ApiResponse.success("File uploaded", imageDto);
        return ResponseEntity.ok(body);
    }

    // deleteImage
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() && @imageServiceImpl.isOwner(#id, #userId)")
    public ResponseEntity<?> deleteImage(@PathVariable Long id, @CurrentUserUUID UUID userId) {
        imageService.deleteImage(id, userId);
        var body = ApiResponse.success("Image deleted");

        log.info("User(id={}) deleted image(id={})", userId, id);
        return ResponseEntity.ok(body);
    }

    // getImage
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() && @imageServiceImpl.isOwner(#id, #userId)")
    public ResponseEntity<?> getImage(@PathVariable Long id, @CurrentUserUUID UUID userId) {
        ImageDto image = imageService.getImage(id);
        var body = ApiResponse.success("Image found", image);
        return ResponseEntity.ok(body);
    }


}
