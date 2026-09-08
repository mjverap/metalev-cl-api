package cl.mjvera.metalevcl.infrastructure.persistence.repository;

import cl.mjvera.metalevcl.application.service.RecitalSearchCriteria;
import cl.mjvera.metalevcl.infrastructure.persistence.RecitalEntity;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class RecitalSpecifications {

    private RecitalSpecifications() {
    }

    public static Specification<RecitalEntity> withFilters(RecitalSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (criteria.type() != null) {
                predicates.add(cb.equal(root.get("type"), criteria.type()));
            }
            if (criteria.status() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.venueId() != null) {
                predicates.add(cb.equal(root.get("venue").get("id"), criteria.venueId()));
            }
            if (criteria.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("minTicketPrice"), criteria.minPrice()));
            }
            if (criteria.maxPrice() != null) {
                Expression<Integer> effectiveMaxPrice = cb.coalesce(root.get("maxTicketPrice"), root.get("minTicketPrice"));
                predicates.add(cb.lessThanOrEqualTo(effectiveMaxPrice, criteria.maxPrice()));
            }
            if (criteria.startDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), criteria.startDateFrom()));
            }
            if (criteria.endDateTo() != null) {
                Expression<java.time.LocalDate> effectiveEndDate = cb.coalesce(root.get("endDate"), root.get("startDate"));
                predicates.add(cb.lessThanOrEqualTo(effectiveEndDate, criteria.endDateTo()));
            }

            // Extension point: future band filter should move here once Band is a first-class entity.
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
