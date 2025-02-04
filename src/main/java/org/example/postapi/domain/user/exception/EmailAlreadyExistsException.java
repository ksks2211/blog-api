package org.example.postapi.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-01-15
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends  RuntimeException{
    public EmailAlreadyExistsException(String email){
        super("Email already exists: "+email);
    }
}
