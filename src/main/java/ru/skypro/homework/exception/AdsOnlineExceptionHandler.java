package ru.skypro.homework.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdsOnlineExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleNotFoundException(NotFoundException notFoundException) {
        return new ResponseEntity<>(notFoundException.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleForbiddenException(ForbiddenException forbiddenException) {
        return new ResponseEntity<>(forbiddenException.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException unauthorizedException) {
        return new ResponseEntity<>(unauthorizedException.getStatus());
    }
}
