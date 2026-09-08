package cl.mjvera.metalevcl.infrastructure.web.dto;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RecitalFilterRequestDtoTest {

    @Test
    void shouldHaveNullDefaults() {
        RecitalFilterRequestDto dto = new RecitalFilterRequestDto();

        assertNull(dto.getType());
        assertNull(dto.getStatus());
        assertNull(dto.getVenueId());
        assertNull(dto.getMinPrice());
        assertNull(dto.getMaxPrice());
        assertNull(dto.getStartDateFrom());
        assertNull(dto.getEndDateTo());
    }

    @Test
    void shouldSetAndGetAllFields() {
        RecitalFilterRequestDto dto = new RecitalFilterRequestDto();
        LocalDate startDateFrom = LocalDate.of(2028, 1, 10);
        LocalDate endDateTo = LocalDate.of(2028, 1, 20);

        dto.setType(RecitalType.FESTIVAL);
        dto.setStatus(RecitalStatus.SOLD_OUT);
        dto.setVenueId(20L);
        dto.setMinPrice(5000);
        dto.setMaxPrice(12000);
        dto.setStartDateFrom(startDateFrom);
        dto.setEndDateTo(endDateTo);

        assertEquals(RecitalType.FESTIVAL, dto.getType());
        assertEquals(RecitalStatus.SOLD_OUT, dto.getStatus());
        assertEquals(20L, dto.getVenueId());
        assertEquals(5000, dto.getMinPrice());
        assertEquals(12000, dto.getMaxPrice());
        assertEquals(startDateFrom, dto.getStartDateFrom());
        assertEquals(endDateTo, dto.getEndDateTo());
    }
}
