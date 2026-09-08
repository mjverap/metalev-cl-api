package cl.mjvera.metalevcl.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DomainExceptionTest {

    @Test
    void businessConflictException_shouldPreserveMessage() {
        BusinessConflictException exception = new BusinessConflictException("venue already exists");

        assertInstanceOf(BusinessConflictException.class, exception);
        assertEquals("venue already exists", exception.getMessage());
    }

    @Test
    void businessValidationException_shouldPreserveMessage() {
        BusinessValidationException exception = new BusinessValidationException("minPrice must be greater than 0");

        assertInstanceOf(BusinessValidationException.class, exception);
        assertEquals("minPrice must be greater than 0", exception.getMessage());
    }

    @Test
    void invalidDateRangeException_shouldPreserveMessage() {
        InvalidDateRangeException exception = new InvalidDateRangeException("startDate must be before endDate");

        assertInstanceOf(InvalidDateRangeException.class, exception);
        assertEquals("startDate must be before endDate", exception.getMessage());
    }

    @Test
    void invalidPriceRangeException_shouldPreserveMessage() {
        InvalidPriceRangeException exception = new InvalidPriceRangeException("minPrice must be less than maxPrice");

        assertInstanceOf(InvalidPriceRangeException.class, exception);
        assertEquals("minPrice must be less than maxPrice", exception.getMessage());
    }

    @Test
    void invalidRecitalInfoException_shouldPreserveMessage() {
        InvalidRecitalInfoException exception = new InvalidRecitalInfoException("invalid recital info");

        assertInstanceOf(InvalidRecitalInfoException.class, exception);
        assertEquals("invalid recital info", exception.getMessage());
    }

    @Test
    void resourceNotFoundException_shouldPreserveMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("venue not found with id: 5");

        assertInstanceOf(ResourceNotFoundException.class, exception);
        assertEquals("venue not found with id: 5", exception.getMessage());
    }
}
