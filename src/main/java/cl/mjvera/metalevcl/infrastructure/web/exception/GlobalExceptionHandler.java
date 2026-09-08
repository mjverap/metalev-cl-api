package cl.mjvera.metalevcl.infrastructure.web.exception;

import cl.mjvera.metalevcl.domain.exception.BusinessConflictException;
import cl.mjvera.metalevcl.domain.exception.BusinessValidationException;
import cl.mjvera.metalevcl.domain.exception.InvalidDateRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidPriceRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidRecitalInfoException;
import cl.mjvera.metalevcl.domain.exception.ResourceNotFoundException;
import cl.mjvera.metalevcl.infrastructure.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler({
            BusinessValidationException.class,
            InvalidRecitalInfoException.class,
            InvalidDateRangeException.class,
            InvalidPriceRangeException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse> handleBadRequest(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ApiResponse> handleConflict(BusinessConflictException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), exception.getMessage()));
    }
}
