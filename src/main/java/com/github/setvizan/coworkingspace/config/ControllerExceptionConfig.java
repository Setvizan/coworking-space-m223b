package com.github.setvizan.coworkingspace.config;

import com.github.setvizan.coworkingspace.exceptions.BookingNotFoundException;
import com.github.setvizan.coworkingspace.exceptions.MemberHasBookingsException;
import com.github.setvizan.coworkingspace.exceptions.MemberNotFoundException;
import com.github.setvizan.coworkingspace.exceptions.NoPermissionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionConfig {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleException(MemberNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(MemberHasBookingsException.class)
    public ResponseEntity<String> handleException(MemberHasBookingsException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleException(BookingNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<String> handleException(NoPermissionException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}
