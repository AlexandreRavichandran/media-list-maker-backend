package com.medialistmaker.music.exception.servicenotavailableexception;

import com.medialistmaker.music.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServiceNotAvailableExceptionHandler {

    @ExceptionHandler(ServiceNotAvailableException.class)
    public ResponseEntity<ErrorDTO> generateCustomServiceNotAvailableException(ServiceNotAvailableException e) {
        ErrorDTO errorDTO = ErrorDTO
                .builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorDTO, HttpStatus.FAILED_DEPENDENCY);
    }

}
