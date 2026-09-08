package cl.mjvera.metalevcl.application.validation;

import cl.mjvera.metalevcl.domain.exception.BusinessValidationException;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class RecitalBusinessValidator {

    private static final LocalDate MIN_ALLOWED_DATE = LocalDate.of(1900, 1, 1);
    private static final int MAX_ALLOWED_PRICE = 10_000_000;
    private static final int MIN_BAND_NAME_LENGTH = 1;
    private static final int MAX_BAND_NAME_LENGTH = 100;

    public void validateCreateInput(
            String name,
            Double minPrice,
            Double maxPrice,
            String startDate,
            String endDate,
            Long venueId,
            List<String> bands,
            RecitalType type,
            RecitalStatus status,
            String recitalLink
    ) {
        validateName(name);
        validateVenueId(venueId);
        validatePriceRange(minPrice, maxPrice);
        parseAndValidateDateWindowOrStartDate(startDate, endDate);
        validateBandsByType(bands, type);
        validateStatus(status);
        validateRecitalLink(recitalLink);
    }

    public void validateUpdateInput(
            Double minPrice,
            Double maxPrice,
            String startDate,
            String endDate,
            List<String> bands,
            RecitalType type,
            RecitalStatus status,
            String recitalLink
    ) {
        if (minPrice != null || maxPrice != null) {
            if (minPrice == null || maxPrice == null) {
                throw new BusinessValidationException("minPrice and maxPrice must be sent together.");
            }
            validatePriceRange(minPrice, maxPrice);
        }
        if (startDate != null || endDate != null) {
            if (startDate == null || endDate == null) {
                throw new BusinessValidationException("startDate and endDate must be sent together.");
            }
            parseAndValidateDateWindow(startDate, endDate);
        }
        if (bands != null) {
            validateBandsByType(bands, type);
        }
        validateStatus(status);
        validateRecitalLink(recitalLink);
    }

    public void validatePriceRange(Double minPrice, Double maxPrice) {
        int min = validateAndConvertPrice(minPrice, "minPrice");
        if (min <= 0) {
            throw new BusinessValidationException("minPrice must be greater than 0.");
        }
        if (maxPrice == null) {
            return;
        }
        int max = validateAndConvertPrice(maxPrice, "maxPrice");
        if (max > MAX_ALLOWED_PRICE) {
            throw new BusinessValidationException("maxPrice must be less than or equal to 10000000.");
        }
        if (min >= max) {
            throw new BusinessValidationException("minPrice must be less than maxPrice.");
        }
    }

    public DateWindow parseAndValidateDateWindow(String startDate, String endDate) {
        LocalDate parsedStartDate = parseDate(startDate, "startDate");
        LocalDate parsedEndDate = parseDate(endDate, "endDate");
        validateDateRange(parsedStartDate, parsedEndDate);
        return new DateWindow(parsedStartDate, parsedEndDate);
    }

    public DateWindow parseAndValidateDateWindowOrStartDate(String startDate, String endDate) {
        LocalDate parsedStartDate = parseAndValidateStartDate(startDate);
        if (endDate == null || endDate.isBlank()) {
            return new DateWindow(parsedStartDate, null);
        }
        LocalDate parsedEndDate = parseDate(endDate, "endDate");
        validateDateRange(parsedStartDate, parsedEndDate);
        return new DateWindow(parsedStartDate, parsedEndDate);
    }

    public LocalDate parseAndValidateStartDate(String startDate) {
        LocalDate parsedStartDate = parseDate(startDate, "startDate");
        if (parsedStartDate.isBefore(MIN_ALLOWED_DATE)) {
            throw new BusinessValidationException("startDate must be greater than or equal to 1900-01-01.");
        }
        return parsedStartDate;
    }

    public RecitalStatus resolveDefaultStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate referenceDate = endDate == null ? startDate : endDate;
        return referenceDate.isBefore(LocalDate.now()) ? RecitalStatus.PAST : RecitalStatus.UPCOMING;
    }

    public void validateBandsByType(List<String> bands, RecitalType type) {
        if (type == null) {
            throw new BusinessValidationException("type is required.");
        }
        if (bands == null) {
            throw new BusinessValidationException("bands is required.");
        }
        if (type != RecitalType.FESTIVAL && bands.isEmpty()) {
            throw new BusinessValidationException("bands must contain at least one band unless type is FESTIVAL.");
        }
        for (String bandName : bands) {
            if (bandName == null) {
                throw new BusinessValidationException("band name cannot be null.");
            }
            String normalizedBandName = bandName.trim();
            if (normalizedBandName.isEmpty() || normalizedBandName.length() > MAX_BAND_NAME_LENGTH) {
                throw new BusinessValidationException("each band name must have between 1 and 100 characters.");
            }
        }
    }

    public void validateStatus(RecitalStatus status) {
        if (status == null) {
            return;
        }
        switch (status) {
            case PAST, SOLD_OUT, POSTPONED, CANCELED, UPCOMING -> {
            }
        }
    }

    public void validateRecitalLink(String recitalLink) {
        if (recitalLink == null || recitalLink.isBlank()) {
            return;
        }
        try {
            URI uri = URI.create(recitalLink);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new BusinessValidationException("recitalLink must be a valid URL.");
            }
        } catch (IllegalArgumentException exception) {
            throw new BusinessValidationException("recitalLink must be a valid URL.");
        }
    }

    public int validateAndConvertPrice(Double price, String fieldName) {
        if (price == null) {
            throw new BusinessValidationException(fieldName + " is required.");
        }
        if (!Double.isFinite(price)) {
            throw new BusinessValidationException(fieldName + " must be a finite number.");
        }
        if (price != Math.rint(price)) {
            throw new BusinessValidationException(fieldName + " must be a whole number.");
        }
        return price.intValue();
    }

    public void validateVenueId(Long venueId) {
        if (venueId == null || venueId <= 0) {
            throw new BusinessValidationException("venueId is required and must be greater than 0.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessValidationException("name is required.");
        }
    }

    private LocalDate parseDate(String dateAsText, String fieldName) {
        if (dateAsText == null || dateAsText.isBlank()) {
            throw new BusinessValidationException(fieldName + " is required.");
        }
        try {
            return LocalDate.parse(dateAsText);
        } catch (DateTimeParseException exception) {
            throw new BusinessValidationException(fieldName + " must use format yyyy-MM-dd.");
        }
    }

    private void validateDateRange(LocalDate parsedStartDate, LocalDate parsedEndDate) {
        if (parsedStartDate.isBefore(MIN_ALLOWED_DATE)) {
            throw new BusinessValidationException("startDate must be greater than or equal to 1900-01-01.");
        }
        if (!parsedStartDate.isBefore(parsedEndDate)) {
            throw new BusinessValidationException("startDate must be before endDate.");
        }
        if (parsedEndDate.isAfter(LocalDate.now().plusYears(10))) {
            throw new BusinessValidationException("endDate must be less than or equal to today plus 10 years.");
        }
    }

    public record DateWindow(LocalDate startDate, LocalDate endDate) {
    }
}
