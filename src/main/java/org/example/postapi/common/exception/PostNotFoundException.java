package org.example.postapi.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-01-14
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long postId) {
        super("Post with id " + postId + " not found");
    }
}
