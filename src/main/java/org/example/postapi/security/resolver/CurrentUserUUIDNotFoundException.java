package org.example.postapi.security.resolver;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-01-30
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CurrentUserUUIDNotFoundException extends RuntimeException {
    public CurrentUserUUIDNotFoundException(String message) {
        super(message);
    }
}
