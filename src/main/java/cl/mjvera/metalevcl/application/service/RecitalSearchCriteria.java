package cl.mjvera.metalevcl.application.service;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;

import java.time.LocalDate;

public record RecitalSearchCriteria(
        RecitalType type,
        RecitalStatus status,
        Long venueId,
        Integer minPrice,
        Integer maxPrice,
        LocalDate startDateFrom,
        LocalDate endDateTo
) {
}
