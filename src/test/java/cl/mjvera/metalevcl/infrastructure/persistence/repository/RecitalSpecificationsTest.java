package cl.mjvera.metalevcl.infrastructure.persistence.repository;

import cl.mjvera.metalevcl.application.service.RecitalSearchCriteria;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import cl.mjvera.metalevcl.infrastructure.persistence.RecitalEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Constructor;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecitalSpecificationsTest {

    @Test
    void withFilters_shouldBuildPredicateWithAllFilters() {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                RecitalType.FESTIVAL,
                RecitalStatus.UPCOMING,
                10L,
                5000,
                15000,
                LocalDate.of(2028, 1, 10),
                LocalDate.of(2028, 1, 20)
        );

        @SuppressWarnings("unchecked")
        Root<RecitalEntity> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<Object> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        @SuppressWarnings("unchecked")
        Path<Object> typePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> statusPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> venuePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> venueIdPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Integer> minTicketPricePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Integer> maxTicketPricePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<LocalDate> startDatePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<LocalDate> endDatePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Expression<Integer> effectiveMaxPrice = mock(Expression.class);
        @SuppressWarnings("unchecked")
        Expression<LocalDate> effectiveEndDate = mock(Expression.class);

        Predicate typePredicate = mock(Predicate.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate venuePredicate = mock(Predicate.class);
        Predicate minPricePredicate = mock(Predicate.class);
        Predicate maxPricePredicate = mock(Predicate.class);
        Predicate startDatePredicate = mock(Predicate.class);
        Predicate endDatePredicate = mock(Predicate.class);
        Predicate andPredicate = mock(Predicate.class);

        when(root.get("type")).thenReturn(typePath);
        when(root.get("status")).thenReturn(statusPath);
        when(root.get("venue")).thenReturn(venuePath);
        when(venuePath.get("id")).thenReturn(venueIdPath);
        when(root.get("minTicketPrice")).thenReturn((Path) minTicketPricePath);
        when(root.get("maxTicketPrice")).thenReturn((Path) maxTicketPricePath);
        when(root.get("startDate")).thenReturn((Path) startDatePath);
        when(root.get("endDate")).thenReturn((Path) endDatePath);

        when(cb.equal(typePath, RecitalType.FESTIVAL)).thenReturn(typePredicate);
        when(cb.equal(statusPath, RecitalStatus.UPCOMING)).thenReturn(statusPredicate);
        when(cb.equal(venueIdPath, 10L)).thenReturn(venuePredicate);
        when(cb.greaterThanOrEqualTo(minTicketPricePath, 5000)).thenReturn(minPricePredicate);
        when(cb.coalesce(maxTicketPricePath, minTicketPricePath)).thenReturn(effectiveMaxPrice);
        when(cb.lessThanOrEqualTo(effectiveMaxPrice, 15000)).thenReturn(maxPricePredicate);
        when(cb.greaterThanOrEqualTo(startDatePath, LocalDate.of(2028, 1, 10))).thenReturn(startDatePredicate);
        when(cb.coalesce(endDatePath, startDatePath)).thenReturn(effectiveEndDate);
        when(cb.lessThanOrEqualTo(effectiveEndDate, LocalDate.of(2028, 1, 20))).thenReturn(endDatePredicate);
        when(cb.and(any(Predicate[].class))).thenReturn(andPredicate);

        Specification<RecitalEntity> specification = RecitalSpecifications.withFilters(criteria);
        Predicate result = specification.toPredicate(root, query, cb);

        assertEquals(andPredicate, result);
        verify(cb).equal(typePath, RecitalType.FESTIVAL);
        verify(cb).equal(statusPath, RecitalStatus.UPCOMING);
        verify(cb).equal(venueIdPath, 10L);
        verify(cb).greaterThanOrEqualTo(minTicketPricePath, 5000);
        verify(cb).coalesce(maxTicketPricePath, minTicketPricePath);
        verify(cb).lessThanOrEqualTo(effectiveMaxPrice, 15000);
        verify(cb).greaterThanOrEqualTo(startDatePath, LocalDate.of(2028, 1, 10));
        verify(cb).coalesce(endDatePath, startDatePath);
        verify(cb).lessThanOrEqualTo(effectiveEndDate, LocalDate.of(2028, 1, 20));
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void withFilters_shouldReturnAndPredicateWhenNoFiltersProvided() {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(null, null, null, null, null, null, null);
        @SuppressWarnings("unchecked")
        Root<RecitalEntity> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<Object> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate andPredicate = mock(Predicate.class);
        when(cb.and(any(Predicate[].class))).thenReturn(andPredicate);

        Specification<RecitalEntity> specification = RecitalSpecifications.withFilters(criteria);
        Predicate result = specification.toPredicate(root, query, cb);

        assertEquals(andPredicate, result);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void constructor_shouldBePrivateForUtilityClass() throws Exception {
        Constructor<RecitalSpecifications> constructor = RecitalSpecifications.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        RecitalSpecifications instance = constructor.newInstance();

        assertNotNull(instance);
    }
}
