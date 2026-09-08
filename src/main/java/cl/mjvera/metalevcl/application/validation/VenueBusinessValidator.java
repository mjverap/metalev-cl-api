package cl.mjvera.metalevcl.application.validation;

import cl.mjvera.metalevcl.domain.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

@Component
public class VenueBusinessValidator {

    private static final int MIN_VENUE_NAME_LENGTH = 1;
    private static final int MAX_VENUE_NAME_LENGTH = 100;

    public void validateCreateInput(String name, String street, Long cityId) {
        validateVenueName(name);
        validateAddress(street, cityId);
    }

    public void validateAddress(String street, Long cityId) {
        if (street == null || street.isBlank()) {
            throw new BusinessValidationException("address.street is required.");
        }
        if (cityId == null || cityId <= 0) {
            throw new BusinessValidationException("address.cityId must be greater than 0.");
        }
    }

    public void validateVenueName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessValidationException("name is required.");
        }
        int nameLength = name.trim().length();
        if (nameLength < MIN_VENUE_NAME_LENGTH || nameLength > MAX_VENUE_NAME_LENGTH) {
            throw new BusinessValidationException("name length must be between 1 and 100.");
        }
    }
}
