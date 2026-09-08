package cl.mjvera.metalevcl.domain.model;

import cl.mjvera.metalevcl.domain.exception.InvalidRecitalInfoException;
import cl.mjvera.metalevcl.domain.valueobject.Address;
import cl.mjvera.metalevcl.domain.valueobject.BandList;
import cl.mjvera.metalevcl.domain.valueobject.DateRange;
import cl.mjvera.metalevcl.domain.valueobject.PriceRange;
import cl.mjvera.metalevcl.domain.valueobject.RecitalName;
import cl.mjvera.metalevcl.domain.valueobject.VenueName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainValueObjectsTest {

    @Test
    void address_shouldCreateAndFormatToString() {
        Address address = new Address("Av. Principal 123", buildCity());

        assertEquals("Av. Principal 123", address.street());
        assertEquals("Av. Principal 123, Santiago, Metropolitana", address.toString());
    }

    @Test
    void address_shouldThrowWhenStreetIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Address(null, buildCity()));
    }

    @Test
    void address_shouldThrowWhenStreetIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Address("   ", buildCity()));
    }

    @Test
    void address_shouldThrowWhenCityIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Address("Av. Principal 123", null));
    }

    @Test
    void bandList_shouldCreateImmutableCopyWithValidNames() {
        List<String> original = new ArrayList<>(List.of("Banda A", "Banda B"));
        BandList bandList = new BandList(original);
        original.add("Banda C");

        assertEquals(List.of("Banda A", "Banda B"), bandList.value());
        assertThrows(UnsupportedOperationException.class, () -> bandList.value().add("Banda X"));
    }

    @Test
    void bandList_shouldThrowWhenListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BandList(null));
    }

    @Test
    void bandList_shouldThrowWhenContainsBlankBandName() {
        assertThrows(IllegalArgumentException.class, () -> new BandList(List.of("Banda A", " ")));
    }

    @Test
    void bandList_shouldThrowWhenContainsNullBandName() {
        assertThrows(IllegalArgumentException.class, () -> new BandList(java.util.Collections.singletonList(null)));
    }

    @Test
    void bandList_shouldThrowWhenContainsTooLongBandName() {
        assertThrows(IllegalArgumentException.class, () -> new BandList(List.of("a".repeat(101))));
    }

    @Test
    void dateRange_shouldCreateWhenDatesAreValid() {
        DateRange dateRange = new DateRange(LocalDate.of(2028, 1, 10), LocalDate.of(2028, 1, 11));

        assertEquals(LocalDate.of(2028, 1, 10), dateRange.startDate());
        assertEquals(LocalDate.of(2028, 1, 11), dateRange.endDate());
    }

    @Test
    void dateRange_shouldThrowWhenStartDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DateRange(null, LocalDate.of(2028, 1, 11)));
    }

    @Test
    void dateRange_shouldThrowWhenEndDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DateRange(LocalDate.of(2028, 1, 10), null));
    }

    @Test
    void dateRange_shouldThrowWhenStartDateIsAfterEndDate() {
        assertThrows(IllegalArgumentException.class, () -> new DateRange(LocalDate.of(2028, 1, 12), LocalDate.of(2028, 1, 11)));
    }

    @Test
    void priceRange_shouldCreateWhenValuesAreValid() {
        PriceRange priceRange = new PriceRange(5000, 10000);

        assertEquals(5000, priceRange.minPrice());
        assertEquals(10000, priceRange.maxPrice());
    }

    @Test
    void priceRange_shouldAllowEqualMinAndMax() {
        PriceRange priceRange = new PriceRange(7000, 7000);

        assertEquals(7000, priceRange.minPrice());
        assertEquals(7000, priceRange.maxPrice());
    }

    @Test
    void priceRange_shouldThrowWhenMinPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new PriceRange(-1, 1000));
    }

    @Test
    void priceRange_shouldThrowWhenMaxPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new PriceRange(1000, -1));
    }

    @Test
    void priceRange_shouldThrowWhenMinPriceIsGreaterThanMaxPrice() {
        assertThrows(IllegalArgumentException.class, () -> new PriceRange(1001, 1000));
    }

    @Test
    void recitalName_shouldCreateWithValidValue() {
        RecitalName recitalName = new RecitalName("Festival Rock");

        assertEquals("Festival Rock", recitalName.value());
    }

    @Test
    void recitalName_shouldThrowWhenNull() {
        assertThrows(InvalidRecitalInfoException.class, () -> new RecitalName(null));
    }

    @Test
    void recitalName_shouldThrowWhenBlank() {
        assertThrows(InvalidRecitalInfoException.class, () -> new RecitalName(" "));
    }

    @Test
    void venueName_shouldCreateWithValidValue() {
        VenueName venueName = new VenueName("Teatro Municipal");

        assertEquals("Teatro Municipal", venueName.value());
    }

    @Test
    void venueName_shouldThrowWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> new VenueName(null));
    }

    @Test
    void venueName_shouldThrowWhenBlank() {
        assertThrows(IllegalArgumentException.class, () -> new VenueName(" "));
    }

    private City buildCity() {
        Region region = new Region(1L, "Metropolitana");
        return new City(10L, "Santiago", region);
    }
}
