package cl.mjvera.metalevcl.application.validation;

import cl.mjvera.metalevcl.domain.exception.BusinessValidationException;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BusinessValidatorTest {

    private VenueBusinessValidator venueBusinessValidator;
    private RecitalBusinessValidator recitalBusinessValidator;

    @BeforeEach
    void setUp() {
        venueBusinessValidator = new VenueBusinessValidator();
        recitalBusinessValidator = new RecitalBusinessValidator();
    }

    @Test
    void venueValidateCreateInput_shouldAcceptValidInput() {
        assertDoesNotThrow(() -> venueBusinessValidator.validateCreateInput("Teatro Municipal", "Av. Principal 100", 10L));
    }

    @Test
    void venueValidateCreateInput_shouldThrowWhenNameIsBlank() {
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateCreateInput(" ", "Av. Principal 100", 10L));
    }

    @Test
    void venueValidateAddress_shouldThrowWhenStreetIsBlank() {
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateAddress("  ", 10L));
    }

    @Test
    void venueValidateAddress_shouldThrowWhenStreetIsNull() {
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateAddress(null, 10L));
    }

    @Test
    void venueValidateAddress_shouldThrowWhenCityIdIsInvalid() {
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateAddress("Av. Principal 100", 0L));
    }

    @Test
    void venueValidateAddress_shouldThrowWhenCityIdIsNull() {
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateAddress("Av. Principal 100", null));
    }

    @Test
    void venueValidateVenueName_shouldThrowWhenTooLong() {
        String tooLongName = "a".repeat(101);
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateVenueName(tooLongName));
    }

    @Test
    void venueValidateVenueName_shouldThrowWhenNameIsNull() {
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateVenueName(null));
    }

    @Test
    void venueValidateVenueName_shouldThrowWhenTrimmedLengthIsZeroButNotBlank() {
        assertThrows(BusinessValidationException.class, () -> venueBusinessValidator.validateVenueName("\u0000"));
    }

    @Test
    void venueValidateVenueName_shouldAcceptValidLength() {
        assertDoesNotThrow(() -> venueBusinessValidator.validateVenueName("Venue válido"));
    }

    @Test
    void recitalValidateCreateInput_shouldAcceptValidInput() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validateCreateInput(
                "Recital 2028",
                5000.0,
                15000.0,
                "2028-02-10",
                "2028-02-12",
                5L,
                List.of("Banda A"),
                RecitalType.NATIONAL,
                RecitalStatus.UPCOMING,
                "https://example.com/recital"
        ));
    }

    @Test
    void recitalValidateCreateInput_shouldThrowWhenNameIsNull() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateCreateInput(
                null,
                5000.0,
                15000.0,
                "2028-02-10",
                "2028-02-12",
                5L,
                List.of("Banda A"),
                RecitalType.NATIONAL,
                null,
                null
        ));
    }

    @Test
    void recitalValidateCreateInput_shouldThrowWhenNameIsBlank() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateCreateInput(
                " ",
                5000.0,
                15000.0,
                "2028-02-10",
                "2028-02-12",
                5L,
                List.of("Banda A"),
                RecitalType.NATIONAL,
                null,
                null
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldThrowWhenOnlyOnePriceIsSent() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateUpdateInput(
                5000.0,
                null,
                null,
                null,
                null,
                RecitalType.NATIONAL,
                null,
                null
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldThrowWhenOnlyMaxPriceIsSent() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateUpdateInput(
                null,
                8000.0,
                null,
                null,
                null,
                RecitalType.NATIONAL,
                null,
                null
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldThrowWhenOnlyOneDateIsSent() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateUpdateInput(
                null,
                null,
                "2028-01-10",
                null,
                null,
                RecitalType.NATIONAL,
                null,
                null
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldThrowWhenOnlyEndDateIsSent() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateUpdateInput(
                null,
                null,
                null,
                "2028-01-12",
                null,
                RecitalType.NATIONAL,
                null,
                null
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldAcceptWhenNoOptionalFieldsAreProvided() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validateUpdateInput(
                null,
                null,
                null,
                null,
                null,
                RecitalType.NATIONAL,
                null,
                null
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldThrowWhenBandsPresentAndTypeIsNull() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateUpdateInput(
                null,
                null,
                null,
                null,
                List.of("Banda A"),
                null,
                null,
                null
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldThrowWhenLinkIsInvalid() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateUpdateInput(
                5000.0,
                8000.0,
                "2028-01-10",
                "2028-01-12",
                List.of("Banda A"),
                RecitalType.NATIONAL,
                RecitalStatus.UPCOMING,
                "url-no-valida"
        ));
    }

    @Test
    void recitalValidateUpdateInput_shouldAcceptValidInput() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validateUpdateInput(
                5000.0,
                8000.0,
                "2028-01-10",
                "2028-01-12",
                List.of("Banda A"),
                RecitalType.NATIONAL,
                RecitalStatus.SOLD_OUT,
                "https://example.com/ok"
        ));
    }

    @Test
    void recitalValidatePriceRange_shouldAcceptWhenOnlyMinIsPresent() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validatePriceRange(5000.0, null));
    }

    @Test
    void recitalValidatePriceRange_shouldThrowWhenMinIsNotPositive() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validatePriceRange(0.0, 1000.0));
    }

    @Test
    void recitalValidatePriceRange_shouldThrowWhenMaxIsOverLimit() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validatePriceRange(5000.0, 10_000_001.0));
    }

    @Test
    void recitalValidatePriceRange_shouldThrowWhenMinIsGreaterOrEqualThanMax() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validatePriceRange(5000.0, 5000.0));
    }

    @Test
    void recitalParseAndValidateDateWindow_shouldReturnParsedDates() {
        RecitalBusinessValidator.DateWindow result = recitalBusinessValidator.parseAndValidateDateWindow("2028-03-10", "2028-03-12");
        assertEquals(LocalDate.of(2028, 3, 10), result.startDate());
        assertEquals(LocalDate.of(2028, 3, 12), result.endDate());
    }

    @Test
    void recitalParseAndValidateDateWindow_shouldThrowWhenStartIsAfterEnd() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.parseAndValidateDateWindow("2028-03-12", "2028-03-10"));
    }

    @Test
    void recitalParseAndValidateDateWindow_shouldThrowWhenStartDateIsBeforeMinimumAllowedDate() {
        assertThrows(
                BusinessValidationException.class,
                () -> recitalBusinessValidator.parseAndValidateDateWindow("1899-12-31", "1900-01-02")
        );
    }

    @Test
    void recitalParseAndValidateDateWindow_shouldThrowWhenStartEqualsEnd() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.parseAndValidateDateWindow("2028-03-10", "2028-03-10"));
    }

    @Test
    void recitalParseAndValidateDateWindow_shouldThrowWhenEndDateIsTooFarInFuture() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusYears(10).plusDays(1);
        assertThrows(
                BusinessValidationException.class,
                () -> recitalBusinessValidator.parseAndValidateDateWindow(start.toString(), end.toString())
        );
    }

    @Test
    void recitalParseAndValidateDateWindowOrStartDate_shouldReturnStartOnlyWhenEndIsBlank() {
        RecitalBusinessValidator.DateWindow result = recitalBusinessValidator.parseAndValidateDateWindowOrStartDate("2028-04-10", " ");
        assertEquals(LocalDate.of(2028, 4, 10), result.startDate());
        assertNull(result.endDate());
    }

    @Test
    void recitalParseAndValidateDateWindowOrStartDate_shouldThrowWhenStartDateIsBlank() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.parseAndValidateDateWindowOrStartDate(" ", null));
    }

    @Test
    void recitalParseAndValidateDateWindowOrStartDate_shouldThrowWhenStartDateIsNull() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.parseAndValidateDateWindowOrStartDate(null, null));
    }

    @Test
    void recitalParseAndValidateDateWindowOrStartDate_shouldThrowWhenStartDateHasInvalidFormat() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.parseAndValidateDateWindowOrStartDate("10-04-2028", null));
    }

    @Test
    void recitalParseAndValidateDateWindowOrStartDate_shouldThrowWhenEndDateHasInvalidFormat() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.parseAndValidateDateWindowOrStartDate("2028-04-10", "04/12/2028"));
    }

    @Test
    void recitalParseAndValidateDateWindowOrStartDate_shouldReturnBothDatesWhenEndIsPresent() {
        RecitalBusinessValidator.DateWindow result = recitalBusinessValidator.parseAndValidateDateWindowOrStartDate("2028-04-10", "2028-04-12");
        assertEquals(LocalDate.of(2028, 4, 10), result.startDate());
        assertEquals(LocalDate.of(2028, 4, 12), result.endDate());
    }

    @Test
    void recitalParseAndValidateStartDate_shouldThrowWhenBeforeMinimumDate() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.parseAndValidateStartDate("1899-12-31"));
    }

    @Test
    void recitalResolveDefaultStatus_shouldUseStartDateWhenEndDateIsNull() {
        RecitalStatus result = recitalBusinessValidator.resolveDefaultStatus(LocalDate.now().minusDays(1), null);
        assertEquals(RecitalStatus.PAST, result);
    }

    @Test
    void recitalResolveDefaultStatus_shouldUseEndDateWhenPresent() {
        RecitalStatus result = recitalBusinessValidator.resolveDefaultStatus(LocalDate.now().minusDays(5), LocalDate.now().plusDays(1));
        assertEquals(RecitalStatus.UPCOMING, result);
    }

    @Test
    void recitalValidateBandsByType_shouldAcceptFestivalWithEmptyBands() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validateBandsByType(List.of(), RecitalType.FESTIVAL));
    }

    @Test
    void recitalValidateBandsByType_shouldThrowWhenBandsAreNull() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateBandsByType(null, RecitalType.NATIONAL));
    }

    @Test
    void recitalValidateBandsByType_shouldThrowWhenNonFestivalHasNoBands() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateBandsByType(List.of(), RecitalType.NATIONAL));
    }

    @Test
    void recitalValidateBandsByType_shouldThrowWhenBandNameIsNull() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateBandsByType(java.util.Collections.singletonList(null), RecitalType.NATIONAL));
    }

    @Test
    void recitalValidateBandsByType_shouldThrowWhenBandNameIsBlankAfterTrim() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateBandsByType(List.of("   "), RecitalType.NATIONAL));
    }

    @Test
    void recitalValidateBandsByType_shouldThrowWhenBandNameExceedsMaxLength() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateBandsByType(List.of("a".repeat(101)), RecitalType.NATIONAL));
    }

    @Test
    void recitalValidateStatus_shouldAcceptAllAllowedValuesAndNull() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validateStatus(null));
        assertDoesNotThrow(() -> recitalBusinessValidator.validateStatus(RecitalStatus.PAST));
        assertDoesNotThrow(() -> recitalBusinessValidator.validateStatus(RecitalStatus.SOLD_OUT));
        assertDoesNotThrow(() -> recitalBusinessValidator.validateStatus(RecitalStatus.POSTPONED));
        assertDoesNotThrow(() -> recitalBusinessValidator.validateStatus(RecitalStatus.CANCELED));
        assertDoesNotThrow(() -> recitalBusinessValidator.validateStatus(RecitalStatus.UPCOMING));
    }

    @Test
    void recitalValidateRecitalLink_shouldAcceptNullBlankAndValidUrl() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validateRecitalLink(null));
        assertDoesNotThrow(() -> recitalBusinessValidator.validateRecitalLink("   "));
        assertDoesNotThrow(() -> recitalBusinessValidator.validateRecitalLink("https://example.com/path"));
    }

    @Test
    void recitalValidateRecitalLink_shouldThrowWhenInvalidUrl() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateRecitalLink("invalida"));
    }

    @Test
    void recitalValidateRecitalLink_shouldThrowWhenSchemeExistsButHostMissing() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateRecitalLink("https:///ruta"));
    }

    @Test
    void recitalValidateRecitalLink_shouldThrowWhenUriSyntaxIsInvalid() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateRecitalLink("http://[::1"));
    }

    @Test
    void recitalValidateAndConvertPrice_shouldThrowWhenNull() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateAndConvertPrice(null, "minPrice"));
    }

    @Test
    void recitalValidateAndConvertPrice_shouldThrowWhenNotFinite() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateAndConvertPrice(Double.NaN, "minPrice"));
    }

    @Test
    void recitalValidateAndConvertPrice_shouldThrowWhenNotWholeNumber() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateAndConvertPrice(10.5, "minPrice"));
    }

    @Test
    void recitalValidateAndConvertPrice_shouldConvertValidWholeNumber() {
        int result = recitalBusinessValidator.validateAndConvertPrice(1234.0, "minPrice");
        assertEquals(1234, result);
    }

    @Test
    void recitalValidateVenueId_shouldThrowWhenInvalid() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateVenueId(0L));
    }

    @Test
    void recitalValidateVenueId_shouldThrowWhenNull() {
        assertThrows(BusinessValidationException.class, () -> recitalBusinessValidator.validateVenueId(null));
    }

    @Test
    void recitalValidateVenueId_shouldAcceptValidId() {
        assertDoesNotThrow(() -> recitalBusinessValidator.validateVenueId(1L));
    }
}
