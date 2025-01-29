package org.example.postapi.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-01-28
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class S3FailureException extends RuntimeException{

    public S3FailureException(String message) {
        super(message);
    }
}
