package org.example.postapi.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-01-15
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppUserNotFoundException extends RuntimeException{
    public AppUserNotFoundException(String message){
        super(message);
    }
}
