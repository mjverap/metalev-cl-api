package cl.mjvera.metalevcl.application.service;

import cl.mjvera.metalevcl.application.validation.VenueBusinessValidator;
import cl.mjvera.metalevcl.domain.exception.BusinessConflictException;
import cl.mjvera.metalevcl.domain.exception.ResourceNotFoundException;
import cl.mjvera.metalevcl.domain.model.Venue;
import cl.mjvera.metalevcl.infrastructure.persistence.CityEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.RegionEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.VenueEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.CityJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.RecitalJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.VenueJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VenueServiceImplTest {

    @Mock
    private VenueJpaRepository venueJpaRepository;

    @Mock
    private CityJpaRepository cityJpaRepository;

    @Mock
    private RecitalJpaRepository recitalJpaRepository;

    private VenueServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VenueServiceImpl(venueJpaRepository, cityJpaRepository, recitalJpaRepository, new VenueBusinessValidator());
    }

    @Test
    void createVenue_shouldPersistVenueAndMapCityAndRegion() {
        Long cityId = 11L;
        CityEntity cityEntity = buildCityEntity(cityId, "Santiago", 1L, "Metropolitana");
        when(cityJpaRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
        when(venueJpaRepository.save(any(VenueEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venue result = service.createVenue("Teatro Municipal", "Av. Siempre Viva 123", cityId);

        assertEquals("Teatro Municipal", result.getName());
        assertEquals("Av. Siempre Viva 123", result.getAddress().street());
        assertEquals("Santiago", result.getAddress().city().getName());
        assertEquals("Metropolitana", result.getAddress().city().getRegion().getName());
        verify(venueJpaRepository).save(any(VenueEntity.class));
    }

    @Test
    void createVenue_shouldBuildEntityWithExpectedValues_beforeSaving() {
        Long cityId = 111L;
        CityEntity cityEntity = buildCityEntity(cityId, "Santiago", 1L, "Metropolitana");
        when(cityJpaRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
        when(venueJpaRepository.save(any(VenueEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createVenue("Teatro Captura", "Av. Captura 456", cityId);

        ArgumentCaptor<VenueEntity> captor = ArgumentCaptor.forClass(VenueEntity.class);
        verify(venueJpaRepository).save(captor.capture());
        VenueEntity savedEntity = captor.getValue();
        assertEquals("Teatro Captura", savedEntity.getName());
        assertEquals("Av. Captura 456", savedEntity.getStreet());
        assertSame(cityEntity, savedEntity.getCity());
    }

    @Test
    void getVenues_shouldFilterByRegionAndCity() {
        CityEntity cityEntity = buildCityEntity(21L, "Valparaíso", 2L, "Valparaíso");
        VenueEntity venueEntity = new VenueEntity(31L, "Teatro de la Plaza", "Calle Principal 222", cityEntity);
        when(venueJpaRepository.findByRegionAndCity("valparaiso", "valparaíso")).thenReturn(List.of(venueEntity));

        List<Venue> result = service.getVenues("valparaiso", "valparaíso");

        assertEquals(1, result.size());
        assertEquals("Teatro de la Plaza", result.get(0).getName());
        assertEquals("Calle Principal 222", result.get(0).getAddress().street());
        assertEquals("Valparaíso", result.get(0).getAddress().city().getName());
    }

    @Test
    void getVenues_withoutFilters_shouldReturnAllMappedVenues() {
        CityEntity cityEntity = buildCityEntity(22L, "Santiago", 1L, "Metropolitana");
        VenueEntity venueEntity = new VenueEntity(32L, "Arena", "Calle 123", cityEntity);
        when(venueJpaRepository.findAll()).thenReturn(List.of(venueEntity));

        List<Venue> result = service.getVenues();

        assertEquals(1, result.size());
        assertEquals(32L, result.get(0).getId());
        assertEquals("Arena", result.get(0).getName());
        assertEquals("Santiago", result.get(0).getAddress().city().getName());
    }

    @Test
    void getVenues_shouldNormalizeBlankFiltersToNull() {
        CityEntity cityEntity = buildCityEntity(23L, "Puerto Montt", 2L, "Los Lagos");
        VenueEntity venueEntity = new VenueEntity(33L, "Teatro Sur", "Costanera 100", cityEntity);
        when(venueJpaRepository.findByRegionAndCity(null, null)).thenReturn(List.of(venueEntity));

        List<Venue> result = service.getVenues("   ", "");

        assertEquals(1, result.size());
        assertEquals("Teatro Sur", result.get(0).getName());
    }

    @Test
    void getVenues_shouldPassNullFiltersAsNull() {
        CityEntity cityEntity = buildCityEntity(230L, "Puerto Montt", 2L, "Los Lagos");
        VenueEntity venueEntity = new VenueEntity(330L, "Teatro Sur", "Costanera 100", cityEntity);
        when(venueJpaRepository.findByRegionAndCity(null, null)).thenReturn(List.of(venueEntity));

        List<Venue> result = service.getVenues(null, null);

        assertEquals(1, result.size());
        verify(venueJpaRepository).findByRegionAndCity(null, null);
    }

    @Test
    void getVenues_shouldTrimFiltersBeforeQuerying() {
        CityEntity cityEntity = buildCityEntity(24L, "Puerto Montt", 2L, "Los Lagos");
        VenueEntity venueEntity = new VenueEntity(34L, "Teatro Norte", "Costanera 101", cityEntity);
        when(venueJpaRepository.findByRegionAndCity("Los Lagos", "Puerto Montt")).thenReturn(List.of(venueEntity));

        List<Venue> result = service.getVenues("  Los Lagos  ", "  Puerto Montt  ");

        assertEquals(1, result.size());
        verify(venueJpaRepository).findByRegionAndCity("Los Lagos", "Puerto Montt");
    }

    @Test
    void getVenueById_shouldReturnVenue_whenExists() {
        Long venueId = 77L;
        CityEntity cityEntity = buildCityEntity(10L, "Santiago", 1L, "Metropolitana");
        VenueEntity venueEntity = new VenueEntity(venueId, "Teatro Municipal", "Av. Central 1", cityEntity);
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));

        Venue result = service.getVenueById(venueId);

        assertEquals(venueId, result.getId());
        assertEquals("Teatro Municipal", result.getName());
        assertEquals("Santiago", result.getAddress().city().getName());
    }

    @Test
    void updateVenueAddress_shouldChangeStreetAndCity() {
        Long venueId = 41L;
        Long newCityId = 12L;
        CityEntity currentCity = buildCityEntity(10L, "Santiago", 1L, "Metropolitana");
        CityEntity newCity = buildCityEntity(newCityId, "Concepción", 3L, "Biobío");
        VenueEntity venueEntity = new VenueEntity(venueId, "Teatro Municipal", "Av. Central 1", currentCity);

        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(cityJpaRepository.findById(newCityId)).thenReturn(Optional.of(newCity));
        when(venueJpaRepository.save(any(VenueEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venue result = service.updateVenueAddress(venueId, "Calle Nuevo 456", newCityId);

        assertEquals("Calle Nuevo 456", result.getAddress().street());
        assertEquals("Concepción", result.getAddress().city().getName());
        assertEquals("Biobío", result.getAddress().city().getRegion().getName());
    }

    @Test
    void updateVenueName_shouldUpdateAndPersistName() {
        Long venueId = 61L;
        CityEntity cityEntity = buildCityEntity(10L, "Santiago", 1L, "Metropolitana");
        VenueEntity venueEntity = new VenueEntity(venueId, "Nombre anterior", "Calle 10", cityEntity);
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(venueJpaRepository.save(any(VenueEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venue result = service.updateVenueName(venueId, "Nombre nuevo");

        assertEquals("Nombre nuevo", result.getName());
    }

    @Test
    void createVenue_shouldThrowResourceNotFound_whenCityDoesNotExist() {
        when(cityJpaRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createVenue("Teatro", "Calle 11", 404L));
    }

    @Test
    void updateVenueAddress_shouldThrowResourceNotFound_whenVenueIdIsInvalid() {
        assertThrows(ResourceNotFoundException.class, () -> service.updateVenueAddress(0L, "Calle", 2L));
    }

    @Test
    void updateVenueName_shouldThrowResourceNotFound_whenVenueIdIsNull() {
        assertThrows(ResourceNotFoundException.class, () -> service.updateVenueName(null, "Nombre"));
    }

    @Test
    void updateVenueName_shouldThrowResourceNotFound_whenVenueIdIsZero() {
        assertThrows(ResourceNotFoundException.class, () -> service.updateVenueName(0L, "Nombre"));
    }

    @Test
    void updateVenueName_shouldThrowResourceNotFound_whenVenueDoesNotExist() {
        when(venueJpaRepository.findById(888L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateVenueName(888L, "Nombre"));
    }

    @Test
    void deleteVenue_shouldThrowResourceNotFound_whenVenueDoesNotExist() {
        when(venueJpaRepository.findById(889L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteVenue(889L));
    }

    @Test
    void deleteVenue_shouldDeleteVenue_whenNoAssociatedRecitals() {
        Long venueId = 56L;
        VenueEntity venueEntity = new VenueEntity(venueId, "Teatro Municipal", "Av. Siempre Viva 123", buildCityEntity(14L, "Santiago", 1L, "Metropolitana"));

        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.existsByVenue_Id(venueId)).thenReturn(false);

        service.deleteVenue(venueId);

        verify(venueJpaRepository).delete(venueEntity);
    }

    @Test
    void deleteVenue_shouldThrowConflict_whenVenueHasAssociatedRecitals() {
        Long venueId = 55L;
        VenueEntity venueEntity = new VenueEntity(venueId, "Teatro Municipal", "Av. Siempre Viva 123", buildCityEntity(14L, "Santiago", 1L, "Metropolitana"));

        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.existsByVenue_Id(venueId)).thenReturn(true);

        assertThrows(BusinessConflictException.class, () -> service.deleteVenue(venueId));
    }

    @Test
    void getVenueById_shouldReturnNull_whenVenueDoesNotExist() {
        when(venueJpaRepository.findById(999L)).thenReturn(Optional.empty());

        assertNull(service.getVenueById(999L));
    }

    private CityEntity buildCityEntity(Long cityId, String cityName, Long regionId, String regionName) {
        RegionEntity regionEntity = new RegionEntity(regionId, regionName);
        return new CityEntity(cityId, cityName, regionEntity);
    }
}
