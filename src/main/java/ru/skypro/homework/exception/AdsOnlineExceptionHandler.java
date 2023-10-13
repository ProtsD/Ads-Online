package ru.skypro.homework.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class AdsOnlineExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleException(ResponseStatusException exception) {
        return new ResponseEntity<>(exception.getStatus());
    }
}
