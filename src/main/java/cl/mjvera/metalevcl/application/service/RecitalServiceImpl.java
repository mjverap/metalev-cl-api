package cl.mjvera.metalevcl.application.service;

import cl.mjvera.metalevcl.application.validation.RecitalBusinessValidator;
import cl.mjvera.metalevcl.domain.exception.BusinessValidationException;
import cl.mjvera.metalevcl.domain.model.City;
import cl.mjvera.metalevcl.domain.model.Region;
import cl.mjvera.metalevcl.domain.exception.ResourceNotFoundException;
import cl.mjvera.metalevcl.domain.model.Recital;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import cl.mjvera.metalevcl.domain.model.Venue;
import cl.mjvera.metalevcl.domain.valueobject.Address;
import cl.mjvera.metalevcl.infrastructure.persistence.RecitalEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.VenueEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.RecitalJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.RecitalSpecifications;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.VenueJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecitalServiceImpl implements RecitalService {

    private final RecitalJpaRepository recitalJpaRepository;
    private final VenueJpaRepository venueJpaRepository;
    private final RecitalBusinessValidator recitalBusinessValidator;

    public RecitalServiceImpl(
            RecitalJpaRepository recitalJpaRepository,
            VenueJpaRepository venueJpaRepository,
            RecitalBusinessValidator recitalBusinessValidator
    ) {
        this.recitalJpaRepository = recitalJpaRepository;
        this.venueJpaRepository = venueJpaRepository;
        this.recitalBusinessValidator = recitalBusinessValidator;
    }

    @Override
    public List<Recital> getRecitals() {
        return recitalJpaRepository.findAll()
                .stream()
                .map(this::toDomainRecital)
                .toList();
    }

    @Override
    public List<Recital> getRecitals(RecitalSearchCriteria criteria) {
        validateSearchCriteria(criteria);
        return recitalJpaRepository.findAll(RecitalSpecifications.withFilters(criteria)).stream()
                .map(this::toDomainRecital)
                .toList();
    }

    @Override
    public Recital getRecitalById(Long id) {
        return toDomainRecital(getRecitalEntityById(id));
    }

    @Override
    public Recital createRecital(
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
        RecitalType normalizedType = type == null ? RecitalType.NATIONAL : type;
        recitalBusinessValidator.validateCreateInput(
                name, minPrice, maxPrice, startDate, endDate, venueId, bands, normalizedType, status, recitalLink
        );

        int parsedMinPrice = recitalBusinessValidator.validateAndConvertPrice(minPrice, "minPrice");
        int parsedMaxPrice = maxPrice == null ? parsedMinPrice : recitalBusinessValidator.validateAndConvertPrice(maxPrice, "maxPrice");
        RecitalBusinessValidator.DateWindow dateWindow = recitalBusinessValidator.parseAndValidateDateWindowOrStartDate(startDate, endDate);
        LocalDate normalizedEndDate = dateWindow.endDate() == null ? dateWindow.startDate() : dateWindow.endDate();
        RecitalStatus normalizedStatus = status == null
                ? recitalBusinessValidator.resolveDefaultStatus(dateWindow.startDate(), dateWindow.endDate())
                : status;

        VenueEntity venueEntity = getVenueEntityById(venueId);
        Recital recital = new Recital(name, toDomainVenue(venueEntity), bands);
        recital.updateTicketPriceRange(parsedMinPrice, parsedMaxPrice);
        recital.reprogramTo(dateWindow.startDate(), normalizedEndDate);
        recital.setType(normalizedType);
        recital.setStatus(normalizedStatus);
        recital.setRecitalLink(recitalLink);

        RecitalEntity recitalEntity = toEntity(recital, venueEntity);
        return toDomainRecital(recitalJpaRepository.save(recitalEntity));
    }

    @Override
    public Recital updateRecital(
            Long id,
            List<String> bands,
            Double minPrice,
            Double maxPrice,
            String startDate,
            String endDate,
            RecitalStatus status,
            String recitalLink
    ) {
        Recital recital = toDomainRecital(getRecitalEntityById(id));
        recitalBusinessValidator.validateUpdateInput(
                minPrice, maxPrice, startDate, endDate, bands, recital.getType(), status, recitalLink
        );

        if (bands != null) {
            recital.setBands(bands);
        }
        if (minPrice != null && maxPrice != null) {
            recital.updateTicketPriceRange(
                    recitalBusinessValidator.validateAndConvertPrice(minPrice, "minPrice"),
                    recitalBusinessValidator.validateAndConvertPrice(maxPrice, "maxPrice")
            );
        }
        if (startDate != null && endDate != null) {
            RecitalBusinessValidator.DateWindow dateWindow = recitalBusinessValidator.parseAndValidateDateWindow(startDate, endDate);
            recital.reprogramTo(dateWindow.startDate(), dateWindow.endDate());
            if (status == null) {
                recital.setStatus(recitalBusinessValidator.resolveDefaultStatus(dateWindow.startDate(), dateWindow.endDate()));
            }
        }
        if (status != null) {
            recital.setStatus(status);
        }
        if (recitalLink != null) {
            recital.setRecitalLink(recitalLink.isBlank() ? null : recitalLink);
        }

        return saveRecital(recital);
    }

    @Override
    public Recital updateRecitalVenue(Long id, Long venueId) {
        RecitalEntity recitalEntity = getRecitalEntityById(id);
        VenueEntity venueEntity = getVenueEntityById(venueId);
        Recital recital = toDomainRecital(recitalEntity);
        recital.moveTo(toDomainVenue(venueEntity));
        return toDomainRecital(recitalJpaRepository.save(toEntity(recital, venueEntity)));
    }

    @Override
    public Recital addRecitalBands(Long id, List<String> bands) {
        Recital recital = toDomainRecital(getRecitalEntityById(id));
        recitalBusinessValidator.validateBandsByType(bands, recital.getType());
        for (String band : bands) {
            recital.addBand(band);
        }
        return saveRecital(recital);
    }

    @Override
    public Recital removeRecitalBands(Long id, List<String> bands) {
        Recital recital = toDomainRecital(getRecitalEntityById(id));
        for (String band : bands) {
            recital.removeBand(band);
        }
        return saveRecital(recital);
    }

    @Override
    public Recital updateRecitalTicketPriceRange(Long id, Double minPrice, Double maxPrice) {
        recitalBusinessValidator.validatePriceRange(minPrice, maxPrice);
        Recital recital = toDomainRecital(getRecitalEntityById(id));
        recital.updateTicketPriceRange(
                recitalBusinessValidator.validateAndConvertPrice(minPrice, "minPrice"),
                recitalBusinessValidator.validateAndConvertPrice(maxPrice, "maxPrice")
        );
        return saveRecital(recital);
    }

    @Override
    public Recital updateRecitalDateRange(Long id, String startDate, String endDate) {
        RecitalBusinessValidator.DateWindow dateWindow = recitalBusinessValidator.parseAndValidateDateWindow(startDate, endDate);
        Recital recital = toDomainRecital(getRecitalEntityById(id));
        recital.reprogramTo(dateWindow.startDate(), dateWindow.endDate());
        return saveRecital(recital);
    }

    @Override
    public void deleteRecital(Long id) {
        recitalJpaRepository.delete(getRecitalEntityById(id));
    }

    private Recital toDomainRecital(RecitalEntity recitalEntity) {
        if (recitalEntity.getMinTicketPrice() == null || recitalEntity.getStartDate() == null) {
            Recital recital = new Recital(
                    recitalEntity.getId(),
                    recitalEntity.getName(),
                    toDomainVenue(recitalEntity.getVenue()),
                    recitalEntity.getBands()
            );
            recital.setType(recitalEntity.getType());
            recital.setStatus(recitalEntity.getStatus());
            recital.setRecitalLink(recitalEntity.getRecitalLink());
            return recital;
        }
        return new Recital(
                recitalEntity.getId(),
                recitalEntity.getName(),
                toDomainVenue(recitalEntity.getVenue()),
                recitalEntity.getBands(),
                recitalEntity.getMinTicketPrice(),
                recitalEntity.getMaxTicketPrice() == null ? recitalEntity.getMinTicketPrice() : recitalEntity.getMaxTicketPrice(),
                recitalEntity.getStartDate(),
                recitalEntity.getEndDate() == null ? recitalEntity.getStartDate() : recitalEntity.getEndDate(),
                recitalEntity.getType(),
                recitalEntity.getStatus(),
                recitalEntity.getRecitalLink()
        );
    }

    private Venue toDomainVenue(VenueEntity venueEntity) {
        Region region = new Region(venueEntity.getCity().getRegion().getId(), venueEntity.getCity().getRegion().getName());
        City city = new City(venueEntity.getCity().getId(), venueEntity.getCity().getName(), region);
        return new Venue(
                venueEntity.getId(),
                venueEntity.getName(),
                new Address(venueEntity.getStreet(), city)
        );
    }

    private Recital saveRecital(Recital recital) {
        VenueEntity venueEntity = getVenueEntityById(recital.getVenue().getId());
        RecitalEntity savedRecitalEntity = recitalJpaRepository.save(toEntity(recital, venueEntity));
        return toDomainRecital(savedRecitalEntity);
    }

    private RecitalEntity toEntity(Recital recital, VenueEntity venueEntity) {
        RecitalEntity recitalEntity = recital.getId() == null
                ? new RecitalEntity(recital.getName(), venueEntity, recital.getBands())
                : recitalJpaRepository.findById(recital.getId())
                .orElseGet(() -> new RecitalEntity(
                        recital.getId(),
                        recital.getName(),
                        venueEntity,
                        recital.getBands()
                ));
        recitalEntity.setName(recital.getName());
        recitalEntity.setVenue(venueEntity);
        recitalEntity.setBands(recital.getBands());
        recitalEntity.setType(recital.getType());
        recitalEntity.setStatus(recital.getStatus());
        recitalEntity.setRecitalLink(recital.getRecitalLink());
        applyOptionalState(recitalEntity, recital);
        return recitalEntity;
    }

    private void applyOptionalState(RecitalEntity recitalEntity, Recital recital) {
        if (recital.getTicketPriceRange() != null) {
            recitalEntity.setMinTicketPrice(recital.getMinTicketPrice());
            recitalEntity.setMaxTicketPrice(recital.getMaxTicketPrice());
        }
        if (recital.getDateRange() != null) {
            recitalEntity.setStartDate(recital.getStartDate());
            recitalEntity.setEndDate(recital.getEndDate());
        }
    }

    private RecitalEntity getRecitalEntityById(Long id) {
        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Recital id must be greater than 0.");
        }
        return recitalJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recital not found with id: " + id));
    }

    private VenueEntity getVenueEntityById(Long id) {
        recitalBusinessValidator.validateVenueId(id);
        return venueJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
    }

    private void validateSearchCriteria(RecitalSearchCriteria criteria) {
        if (criteria == null) {
            return;
        }
        if (criteria.venueId() != null && criteria.venueId() <= 0) {
            throw new BusinessValidationException("venueId filter must be greater than 0.");
        }
        if (criteria.minPrice() != null && criteria.minPrice() <= 0) {
            throw new BusinessValidationException("minPrice filter must be greater than 0.");
        }
        if (criteria.maxPrice() != null && criteria.maxPrice() <= 0) {
            throw new BusinessValidationException("maxPrice filter must be greater than 0.");
        }
        if (criteria.minPrice() != null && criteria.maxPrice() != null && criteria.minPrice() > criteria.maxPrice()) {
            throw new BusinessValidationException("minPrice filter must be less than or equal to maxPrice.");
        }
        if (criteria.startDateFrom() != null
                && criteria.endDateTo() != null
                && criteria.startDateFrom().isAfter(criteria.endDateTo())) {
            throw new BusinessValidationException("startDateFrom must be before or equal to endDateTo.");
        }
    }
}
