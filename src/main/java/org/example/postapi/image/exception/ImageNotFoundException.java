package org.example.postapi.image.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-01-28
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(Long imageId) {
        super("Could not find image with id " + imageId);
    }
}
