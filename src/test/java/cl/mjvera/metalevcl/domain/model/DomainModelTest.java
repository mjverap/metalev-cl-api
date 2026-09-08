package cl.mjvera.metalevcl.domain.model;

import cl.mjvera.metalevcl.domain.exception.InvalidDateRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidPriceRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidRecitalInfoException;
import cl.mjvera.metalevcl.domain.valueobject.Address;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainModelTest {

    @Test
    void city_shouldExposeIdNameAndRegion() {
        Region region = new Region(1L, "Metropolitana");

        City city = new City(10L, "Santiago", region);

        assertEquals(10L, city.getId());
        assertEquals("Santiago", city.getName());
        assertEquals(region, city.getRegion());
    }

    @Test
    void region_shouldExposeIdAndName() {
        Region region = new Region(2L, "Biobío");

        assertEquals(2L, region.getId());
        assertEquals("Biobío", region.getName());
    }

    @Test
    void venue_shouldStoreNameAndAddress() {
        Region region = new Region(3L, "Valparaíso");
        City city = new City(30L, "Viña del Mar", region);
        Address address = new Address("Av. Marina 123", city);

        Venue venue = new Venue(5L, "Teatro Municipal", address);

        assertEquals(5L, venue.getId());
        assertEquals("Teatro Municipal", venue.getName());
        assertEquals(address, venue.getAddress());
        assertEquals("Viña del Mar", venue.getAddress().city().getName());
    }

    @Test
    void venue_shouldAllowRenameAndAddressUpdate() {
        Region region = new Region(30L, "Los Lagos");
        City city = new City(300L, "Puerto Montt", region);
        Venue venue = new Venue("Teatro Sur", new Address("Costanera 101", city));

        venue.renameTo("Teatro Austral");
        venue.updateAddress(new Address("Costanera 202", city));
        venue.setName("Teatro Final");

        assertNull(venue.getId());
        assertEquals("Teatro Final", venue.getName());
        assertEquals("Costanera 202", venue.getAddress().street());
    }

    @Test
    void recital_shouldCreateWithBandsPricesDatesAndStatus() {
        Region region = new Region(4L, "RM");
        City city = new City(40L, "Providencia", region);
        Venue venue = new Venue(6L, "Arena", new Address("Calle Uno 100", city));

        Recital recital = new Recital(
                7L,
                "Festival Rock",
                venue,
                List.of("Banda A", "Banda B"),
                5000,
                12000,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 3),
                RecitalType.NATIONAL,
                RecitalStatus.UPCOMING,
                "https://example.com/festival"
        );

        assertEquals(7L, recital.getId());
        assertEquals("Festival Rock", recital.getName());
        assertEquals(List.of("Banda A", "Banda B"), recital.getBands());
        assertEquals(5000, recital.getMinTicketPrice());
        assertEquals(12000, recital.getMaxTicketPrice());
        assertEquals(LocalDate.of(2026, 5, 1), recital.getStartDate());
        assertEquals(LocalDate.of(2026, 5, 3), recital.getEndDate());
        assertEquals(RecitalType.NATIONAL, recital.getType());
        assertEquals(RecitalStatus.UPCOMING, recital.getStatus());
        assertEquals("https://example.com/festival", recital.getRecitalLink());
    }

    @Test
    void recital_defaultConstructor_shouldSetSafeDefaults() {
        Recital recital = new Recital();

        assertNull(recital.getId());
        assertEquals("", recital.getName());
        assertEquals(RecitalType.NATIONAL, recital.getType());
        assertEquals(RecitalStatus.UPCOMING, recital.getStatus());
        assertEquals(0, recital.getMinTicketPrice());
        assertEquals(0, recital.getMaxTicketPrice());
        assertNull(recital.getStartDate());
        assertNull(recital.getEndDate());
    }

    @Test
    void recital_constructorWithIdNameVenueBands_shouldKeepOptionalStateNull() {
        Venue venue = buildVenue(61L, "Temuco", "Araucanía");
        Recital recital = new Recital(101L, "Recital base", venue, List.of("Banda A"));

        assertEquals(101L, recital.getId());
        assertEquals("Recital base", recital.getName());
        assertNull(recital.getTicketPriceRange());
        assertNull(recital.getDateRange());
        assertEquals(0, recital.getMinTicketPrice());
        assertEquals(0, recital.getMaxTicketPrice());
        assertNull(recital.getStartDate());
        assertNull(recital.getEndDate());
    }

    @Test
    void recital_shouldRejectInvalidRangeValues() {
        Region region = new Region(5L, "RM");
        City city = new City(50L, "Las Condes", region);
        Venue venue = new Venue(8L, "Club", new Address("Calle Dos 200", city));
        Recital recital = new Recital("Nombre válido", venue, List.of("Banda 1"));

        assertThrows(InvalidPriceRangeException.class, () -> recital.updateTicketPriceRange(7000, 5000));
        assertThrows(InvalidDateRangeException.class, () -> recital.reprogramTo(LocalDate.of(2027, 10, 8), LocalDate.of(2027, 10, 7)));
    }

    @Test
    void recital_shouldThrowInvalidRecitalInfo_whenVenueIsNullInConstructor() {
        assertThrows(InvalidRecitalInfoException.class, () -> new Recital("Recital", null, List.of("Banda A")));
    }

    @Test
    void recital_shouldThrowInvalidRecitalInfo_whenNameIsInvalidInConstructor() {
        Venue venue = buildVenue(62L, "Santiago", "Metropolitana");
        assertThrows(InvalidRecitalInfoException.class, () -> new Recital(" ", venue, List.of("Banda A")));
    }

    @Test
    void recital_shouldThrowInvalidRecitalInfo_whenBandsAreInvalidInConstructor() {
        Venue venue = buildVenue(63L, "Santiago", "Metropolitana");
        assertThrows(InvalidRecitalInfoException.class, () -> new Recital("Recital válido", venue, List.of(" ")));
    }

    @Test
    void recital_shouldThrowInvalidRecitalInfo_whenVenueIsNullOnMoveAndSetVenue() {
        Recital recital = new Recital("Recital", buildVenue(64L, "Santiago", "Metropolitana"), List.of("Banda A"));

        assertThrows(InvalidRecitalInfoException.class, () -> recital.moveTo(null));
        assertThrows(InvalidRecitalInfoException.class, () -> recital.setVenue(null));
    }

    @Test
    void recital_setVenue_shouldUpdateVenue_whenValueIsValid() {
        Recital recital = new Recital("Recital", buildVenue(67L, "Santiago", "Metropolitana"), List.of("Banda A"));
        Venue newVenue = buildVenue(68L, "Valdivia", "Los Ríos");

        recital.setVenue(newVenue);

        assertEquals(68L, recital.getVenue().getId());
        assertEquals("Valdivia", recital.getVenue().getAddress().city().getName());
    }

    @Test
    void recital_fullConstructor_shouldDefaultTypeAndStatusWhenNull() {
        Venue venue = buildVenue(69L, "Concepción", "Biobío");

        Recital recital = new Recital(
                200L,
                "Recital completo",
                venue,
                List.of("Banda Única"),
                9000,
                12000,
                LocalDate.of(2029, 1, 10),
                LocalDate.of(2029, 1, 11),
                null,
                null,
                "https://example.com/full"
        );

        assertEquals(RecitalType.NATIONAL, recital.getType());
        assertEquals(RecitalStatus.UPCOMING, recital.getStatus());
        assertEquals(200L, recital.getId());
    }

    @Test
    void recital_fullConstructor_shouldThrowInvalidRecitalInfo_whenVenueIsNull() {
        assertThrows(
                InvalidRecitalInfoException.class,
                () -> new Recital(
                        201L,
                        "Recital inválido",
                        null,
                        List.of("Banda A"),
                        1000,
                        2000,
                        LocalDate.of(2029, 1, 10),
                        LocalDate.of(2029, 1, 11),
                        RecitalType.NATIONAL,
                        RecitalStatus.UPCOMING,
                        null
                )
        );
    }

    @Test
    void recital_fullConstructor_shouldWrapIllegalArgumentException_whenPriceRangeIsInvalid() {
        Venue venue = buildVenue(70L, "Santiago", "Metropolitana");

        assertThrows(
                InvalidRecitalInfoException.class,
                () -> new Recital(
                        202L,
                        "Recital inválido precio",
                        venue,
                        List.of("Banda A"),
                        20000,
                        10000,
                        LocalDate.of(2029, 1, 10),
                        LocalDate.of(2029, 1, 11),
                        RecitalType.NATIONAL,
                        RecitalStatus.UPCOMING,
                        null
                )
        );
    }

    @Test
    void recital_shouldThrowInvalidRecitalInfo_whenAddingOrRemovingBlankBand() {
        Recital recital = new Recital("Recital", buildVenue(65L, "Santiago", "Metropolitana"), List.of("Banda A"));

        assertThrows(InvalidRecitalInfoException.class, () -> recital.addBand(" "));
        assertThrows(InvalidRecitalInfoException.class, () -> recital.removeBand(" "));
    }

    @Test
    void recital_shouldThrowInvalidRecitalInfo_whenAddingOrRemovingNullBand() {
        Recital recital = new Recital("Recital", buildVenue(665L, "Santiago", "Metropolitana"), List.of("Banda A"));

        assertThrows(InvalidRecitalInfoException.class, () -> recital.addBand(null));
        assertThrows(InvalidRecitalInfoException.class, () -> recital.removeBand(null));
    }

    @Test
    void recital_removeBand_shouldRemoveExistingBand_whenBandNameIsValid() {
        Recital recital = new Recital("Recital", buildVenue(666L, "Santiago", "Metropolitana"), List.of("Banda A", "Banda B"));

        recital.removeBand("Banda A");

        assertEquals(List.of("Banda B"), recital.getBands());
    }

    @Test
    void recital_shouldAllowMutationsOnVenueBandsStatusAndLink() {
        Region region = new Region(6L, "Araucanía");
        City city = new City(60L, "Temuco", region);
        Venue venue = new Venue(9L, "Auditorio", new Address("Calle Tres 300", city));
        Recital recital = new Recital("Recital", venue, List.of("Banda A"));

        recital.moveTo(new Venue(10L, "Nuevo Auditorio", new Address("Calle Cuatro 400", city)));
        recital.addBand("Banda B");
        recital.setStatus(RecitalStatus.SOLD_OUT);
        recital.setRecitalLink("https://example.com/recital-2");

        assertEquals("Nuevo Auditorio", recital.getVenue().getName());
        assertEquals(List.of("Banda A", "Banda B"), recital.getBands());
        assertEquals(RecitalStatus.SOLD_OUT, recital.getStatus());
        assertEquals("https://example.com/recital-2", recital.getRecitalLink());
    }

    @Test
    void recital_shouldAllowMutationsOnNameTypeStatusPriceDateAndBands() {
        Recital recital = new Recital("Recital inicial", buildVenue(66L, "Concepción", "Biobío"), List.of("Banda A"));

        recital.renameTo("Recital renombrado");
        recital.setName("Recital final");
        recital.setBands(List.of("Banda X", "Banda Y"));
        recital.setType(null);
        recital.setStatus(null);
        recital.setTicketPriceRange(7000, 9000);
        recital.setDateRange(LocalDate.of(2028, 8, 1), LocalDate.of(2028, 8, 2));

        assertEquals("Recital final", recital.getName());
        assertEquals(List.of("Banda X", "Banda Y"), recital.getBands());
        assertEquals(RecitalType.NATIONAL, recital.getType());
        assertEquals(RecitalStatus.UPCOMING, recital.getStatus());
        assertEquals(7000, recital.getMinTicketPrice());
        assertEquals(9000, recital.getMaxTicketPrice());
        assertEquals(LocalDate.of(2028, 8, 1), recital.getStartDate());
        assertEquals(LocalDate.of(2028, 8, 2), recital.getEndDate());
    }

    @Test
    void enumTypes_shouldMatchExpectedValues() {
        assertEquals("NACIONAL", RecitalType.NATIONAL.name());
        assertEquals("INTERNACIONAL", RecitalType.INTERNATIONAL.name());
        assertEquals("FESTIVAL", RecitalType.FESTIVAL.name());

        assertEquals("PAST", RecitalStatus.PAST.name());
        assertEquals("SOLD_OUT", RecitalStatus.SOLD_OUT.name());
        assertEquals("POSTPONED", RecitalStatus.POSTPONED.name());
        assertEquals("CANCELED", RecitalStatus.CANCELED.name());
        assertEquals("UPCOMING", RecitalStatus.UPCOMING.name());
    }

    private Venue buildVenue(Long id, String cityName, String regionName) {
        Region region = new Region(id + 1000, regionName);
        City city = new City(id + 2000, cityName, region);
        return new Venue(id, "Venue " + id, new Address("Street " + id, city));
    }
}
