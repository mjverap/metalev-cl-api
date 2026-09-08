package cl.mjvera.metalevcl.application.service;

import cl.mjvera.metalevcl.domain.model.City;
import cl.mjvera.metalevcl.domain.model.Region;
import cl.mjvera.metalevcl.application.validation.VenueBusinessValidator;
import cl.mjvera.metalevcl.domain.exception.BusinessConflictException;
import cl.mjvera.metalevcl.domain.exception.ResourceNotFoundException;
import cl.mjvera.metalevcl.domain.model.Venue;
import cl.mjvera.metalevcl.domain.valueobject.Address;
import cl.mjvera.metalevcl.infrastructure.persistence.CityEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.VenueEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.CityJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.RecitalJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.VenueJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueJpaRepository venueJpaRepository;
    private final CityJpaRepository cityJpaRepository;
    private final RecitalJpaRepository recitalJpaRepository;
    private final VenueBusinessValidator venueBusinessValidator;

    public VenueServiceImpl(
            VenueJpaRepository venueJpaRepository,
            CityJpaRepository cityJpaRepository,
            RecitalJpaRepository recitalJpaRepository,
            VenueBusinessValidator venueBusinessValidator
    ) {
        this.venueJpaRepository = venueJpaRepository;
        this.cityJpaRepository = cityJpaRepository;
        this.recitalJpaRepository = recitalJpaRepository;
        this.venueBusinessValidator = venueBusinessValidator;
    }

    @Override
    public List<Venue> getVenues() {
        return List.copyOf(venueJpaRepository.findAll())
                .stream()
                .map(this::toDomainVenue)
                .toList();
    }

    @Override
    public List<Venue> getVenues(String region, String city) {
        return List.copyOf(venueJpaRepository.findByRegionAndCity(normalizeFilter(region), normalizeFilter(city)))
                .stream()
                .map(this::toDomainVenue)
                .toList();
    }

    @Override
    public Venue getVenueById(Long id) {
        return venueJpaRepository.findById(id)
                .map(this::toDomainVenue)
                .orElse(null);
    }

    @Override
    public Venue createVenue(String name, String street, Long cityId) {
        venueBusinessValidator.validateCreateInput(name, street, cityId);
        CityEntity cityEntity = getCityEntityById(cityId);
        Venue venue = new Venue(name, new Address(street, toDomainCity(cityEntity)));
        return toDomainVenue(venueJpaRepository.save(toEntity(venue)));
    }

    @Override
    public Venue updateVenueName(Long id, String name) {
        venueBusinessValidator.validateVenueName(name);
        VenueEntity venueEntity = getVenueEntityById(id);
        venueEntity.setName(name);
        return toDomainVenue(venueJpaRepository.save(venueEntity));
    }

    @Override
    public Venue updateVenueAddress(Long id, String street, Long cityId) {
        venueBusinessValidator.validateAddress(street, cityId);
        CityEntity cityEntity = getCityEntityById(cityId);
        VenueEntity venueEntity = getVenueEntityById(id);
        venueEntity.setStreet(street);
        venueEntity.setCity(cityEntity);
        return toDomainVenue(venueJpaRepository.save(venueEntity));
    }

    @Override
    public void deleteVenue(Long id) {
        VenueEntity venueEntity = getVenueEntityById(id);
        if (recitalJpaRepository.existsByVenue_Id(venueEntity.getId())) {
            throw new BusinessConflictException("Venue cannot be deleted because it has associated recitals.");
        }
        venueJpaRepository.delete(venueEntity);
    }

    private Venue toDomainVenue(VenueEntity venueEntity) {
        return new Venue(
                venueEntity.getId(),
                venueEntity.getName(),
                new Address(venueEntity.getStreet(), toDomainCity(venueEntity.getCity()))
        );
    }

    private VenueEntity toEntity(Venue venue) {
        CityEntity cityEntity = getCityEntityById(venue.getAddress().city().getId());
        VenueEntity venueEntity = new VenueEntity(
                venue.getId(),
                venue.getName(),
                venue.getAddress().street(),
                cityEntity
        );
        venueEntity.setName(venue.getName());
        venueEntity.setStreet(venue.getAddress().street());
        venueEntity.setCity(cityEntity);
        return venueEntity;
    }

    private CityEntity getCityEntityById(Long cityId) {
        return cityJpaRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityId));
    }

    private City toDomainCity(CityEntity cityEntity) {
        Region region = new Region(cityEntity.getRegion().getId(), cityEntity.getRegion().getName());
        return new City(cityEntity.getId(), cityEntity.getName(), region);
    }

    private VenueEntity getVenueEntityById(Long id) {
        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Venue id must be greater than 0.");
        }
        return venueJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
    }

    private String normalizeFilter(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
