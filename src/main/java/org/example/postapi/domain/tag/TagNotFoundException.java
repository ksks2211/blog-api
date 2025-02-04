package org.example.postapi.domain.tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rival
 * @since 2025-02-03
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TagNotFoundException extends RuntimeException{

    public TagNotFoundException(final String tagValue) {
        super("Tag not found: " + tagValue);
    }
}
