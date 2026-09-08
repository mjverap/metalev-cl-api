package cl.mjvera.metalevcl.infrastructure.web.exception;

import cl.mjvera.metalevcl.domain.exception.BusinessConflictException;
import cl.mjvera.metalevcl.domain.exception.BusinessValidationException;
import cl.mjvera.metalevcl.domain.exception.InvalidDateRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidPriceRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidRecitalInfoException;
import cl.mjvera.metalevcl.domain.exception.ResourceNotFoundException;
import cl.mjvera.metalevcl.infrastructure.web.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_shouldReturnBadRequestWithJoinedFieldErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("recitalRequestDto", "name", "name is required"),
                new FieldError("recitalRequestDto", "minPrice", "minPrice must be greater than 0")
        ));

        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("name: name is required, minPrice: minPrice must be greater than 0", response.getBody().mensaje());
    }

    @Test
    void handleBadRequest_shouldReturnBadRequestForBusinessValidationException() {
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleBadRequest(
                new BusinessValidationException("Invalid business rule")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("Invalid business rule", response.getBody().mensaje());
    }

    @Test
    void handleBadRequest_shouldReturnBadRequestForInvalidRecitalInfoException() {
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleBadRequest(
                new InvalidRecitalInfoException("Invalid recital info")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid recital info", response.getBody().mensaje());
    }

    @Test
    void handleBadRequest_shouldReturnBadRequestForInvalidDateRangeException() {
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleBadRequest(
                new InvalidDateRangeException("Invalid date range")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date range", response.getBody().mensaje());
    }

    @Test
    void handleBadRequest_shouldReturnBadRequestForInvalidPriceRangeException() {
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleBadRequest(
                new InvalidPriceRangeException("Invalid price range")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid price range", response.getBody().mensaje());
    }

    @Test
    void handleBadRequest_shouldReturnBadRequestForIllegalArgumentException() {
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleBadRequest(
                new IllegalArgumentException("Illegal argument")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Illegal argument", response.getBody().mensaje());
    }

    @Test
    void handleNotFound_shouldReturnNotFound() {
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleNotFound(
                new ResourceNotFoundException("Recital not found")
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("Recital not found", response.getBody().mensaje());
    }

    @Test
    void handleConflict_shouldReturnConflict() {
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleConflict(
                new BusinessConflictException("Venue has associated recitals")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
        assertEquals("Venue has associated recitals", response.getBody().mensaje());
    }
}
