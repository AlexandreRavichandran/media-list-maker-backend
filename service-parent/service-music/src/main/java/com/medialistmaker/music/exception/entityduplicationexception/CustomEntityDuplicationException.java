package com.medialistmaker.music.exception.entityduplicationexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomEntityDuplicationException extends Exception {

    public CustomEntityDuplicationException(String message) {
        super(message);
    }
}
