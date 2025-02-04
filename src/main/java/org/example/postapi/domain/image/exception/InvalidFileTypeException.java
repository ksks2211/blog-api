package org.example.postapi.domain.image.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-01-29
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFileTypeException extends RuntimeException {

    public InvalidFileTypeException(String msg){
        super(msg);
    }
}
