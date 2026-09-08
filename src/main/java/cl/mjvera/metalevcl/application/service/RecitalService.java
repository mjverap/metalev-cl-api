package cl.mjvera.metalevcl.application.service;

import cl.mjvera.metalevcl.domain.model.Recital;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;

import java.util.List;

public interface RecitalService {
    List<Recital> getRecitals();
    List<Recital> getRecitals(RecitalSearchCriteria criteria);
    Recital getRecitalById(Long id);
    Recital createRecital(
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
    );
    Recital updateRecital(
            Long id,
            List<String> bands,
            Double minPrice,
            Double maxPrice,
            String startDate,
            String endDate,
            RecitalStatus status,
            String recitalLink
    );
    Recital updateRecitalVenue(Long id, Long venueId);
    Recital addRecitalBands(Long id, List<String> bands);
    Recital removeRecitalBands(Long id, List<String> bands);
    Recital updateRecitalTicketPriceRange(Long id, Double minPrice, Double maxPrice);
    Recital updateRecitalDateRange(Long id, String startDate, String endDate);
    void deleteRecital(Long id);
}
