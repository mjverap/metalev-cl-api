package cl.mjvera.metalevcl.application.service;

import cl.mjvera.metalevcl.application.validation.RecitalBusinessValidator;
import cl.mjvera.metalevcl.domain.exception.BusinessValidationException;
import cl.mjvera.metalevcl.domain.exception.ResourceNotFoundException;
import cl.mjvera.metalevcl.domain.model.Recital;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import cl.mjvera.metalevcl.infrastructure.persistence.CityEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.RecitalEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.RegionEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.VenueEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.RecitalJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.VenueJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecitalServiceImplTest {

    @Mock
    private RecitalJpaRepository recitalJpaRepository;

    @Mock
    private VenueJpaRepository venueJpaRepository;

    private RecitalBusinessValidator recitalBusinessValidator;
    private RecitalServiceImpl service;

    @BeforeEach
    void setUp() {
        recitalBusinessValidator = new RecitalBusinessValidator();
        service = new RecitalServiceImpl(recitalJpaRepository, venueJpaRepository, recitalBusinessValidator);
    }

    @Test
    void createRecital_shouldSetDefaultUpcomingStatus_whenStatusIsNullAndDatesAreInFuture() {
        Long venueId = 15L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.createRecital(
                "Recital prueba",
                5000.0,
                15000.0,
                LocalDate.now().plusDays(10).toString(),
                null,
                venueId,
                List.of("Banda A", "Banda B"),
                RecitalType.NATIONAL,
                null,
                "https://example.com/recital"
        );

        assertEquals(RecitalStatus.UPCOMING, result.getStatus());
        assertEquals("Recital prueba", result.getName());
        assertEquals(5000, result.getMinTicketPrice());
        assertEquals(15000, result.getMaxTicketPrice());

        ArgumentCaptor<RecitalEntity> recitalCaptor = ArgumentCaptor.forClass(RecitalEntity.class);
        verify(recitalJpaRepository).save(recitalCaptor.capture());
        assertEquals(RecitalStatus.UPCOMING, recitalCaptor.getValue().getStatus());
    }

    @Test
    void createRecital_shouldSetDefaultPastStatus_whenEndDateIsAlreadyInThePast() {
        Long venueId = 20L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.createRecital(
                "Recital pasado",
                3000.0,
                9000.0,
                "2024-01-10",
                "2024-01-15",
                venueId,
                List.of("Banda C"),
                RecitalType.NATIONAL,
                null,
                "https://example.com/pasado"
        );

        assertEquals(RecitalStatus.PAST, result.getStatus());
        assertEquals("Recital pasado", result.getName());
    }

    @Test
    void createRecital_shouldUseDefaultsForTypeMaxPriceAndEndDate_whenOptionalFieldsAreNull() {
        Long venueId = 21L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.createRecital(
                "Recital default",
                6500.0,
                null,
                "2028-03-10",
                null,
                venueId,
                List.of("Banda D"),
                null,
                null,
                null
        );

        assertEquals(RecitalType.NATIONAL, result.getType());
        assertEquals(6500, result.getMinTicketPrice());
        assertEquals(6500, result.getMaxTicketPrice());
        assertEquals(LocalDate.of(2028, 3, 10), result.getStartDate());
        assertEquals(LocalDate.of(2028, 3, 10), result.getEndDate());
    }

    @Test
    void createRecital_shouldThrowResourceNotFound_whenVenueDoesNotExist() {
        when(venueJpaRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createRecital(
                        "Recital sin venue",
                        5000.0,
                        7000.0,
                        "2028-03-10",
                        "2028-03-11",
                        404L,
                        List.of("Banda X"),
                        RecitalType.NATIONAL,
                        RecitalStatus.UPCOMING,
                        null
                )
        );
    }

    @Test
    void updateRecital_shouldResolveDefaultStatusFromDateWindow_whenStatusIsNull() {
        Long recitalId = 3L;
        Long venueId = 3L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity existingEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(existingEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.updateRecital(
                recitalId,
                List.of("Banda Nueva"),
                4000.0,
                7000.0,
                "2028-04-10",
                "2028-04-12",
                null,
                "https://example.com/actualizado"
        );

        assertEquals(RecitalStatus.UPCOMING, result.getStatus());
        assertEquals(List.of("Banda Nueva"), result.getBands());
        assertEquals(4000, result.getMinTicketPrice());
    }

    @Test
    void updateRecital_shouldApplyProvidedStatus_whenStatusIsNotNull() {
        Long recitalId = 31L;
        Long venueId = 3L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity existingEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2027, 4, 10), LocalDate.of(2027, 4, 12), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(existingEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.updateRecital(
                recitalId,
                null,
                null,
                null,
                null,
                null,
                RecitalStatus.CANCELED,
                null
        );

        assertEquals(RecitalStatus.CANCELED, result.getStatus());
    }

    @Test
    void updateRecital_shouldKeepId_whenEntityLookupInsideToEntityReturnsEmpty() {
        Long recitalId = 33L;
        Long venueId = 3L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity existingEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2027, 4, 10), LocalDate.of(2027, 4, 12), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId))
                .thenReturn(Optional.of(existingEntity), Optional.empty());
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.updateRecital(
                recitalId,
                List.of("Banda Nueva"),
                4000.0,
                7000.0,
                "2028-04-10",
                "2028-04-12",
                RecitalStatus.UPCOMING,
                "https://example.com/actualizado"
        );

        assertEquals(recitalId, result.getId());
        assertEquals(List.of("Banda Nueva"), result.getBands());
    }

    @Test
    void getRecitals_withCriteria_shouldReturnMappedDomainRecitals() {
        Long venueId = 9L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(99L, venueEntity, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findAll(any(Specification.class))).thenReturn(List.of(recitalEntity));

        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                RecitalType.NATIONAL,
                RecitalStatus.UPCOMING,
                venueId,
                5000,
                15000,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        List<Recital> result = service.getRecitals(criteria);

        assertEquals(1, result.size());
        assertEquals("Recital prueba", result.get(0).getName());
        assertEquals(venueId, result.get(0).getVenue().getId());
        assertEquals(5000, result.get(0).getMinTicketPrice());
    }

    @Test
    void getRecitals_withNullCriteria_shouldReturnMappedDomainRecitals() {
        Long venueId = 78L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(71L, venueEntity, LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findAll(any(Specification.class))).thenReturn(List.of(recitalEntity));

        List<Recital> result = service.getRecitals((RecitalSearchCriteria) null);

        assertEquals(1, result.size());
        assertEquals(71L, result.get(0).getId());
    }

    @Test
    void getRecitals_withoutCriteria_shouldReturnMappedDomainRecitals() {
        Long venueId = 77L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(70L, venueEntity, LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findAll()).thenReturn(List.of(recitalEntity));

        List<Recital> result = service.getRecitals();

        assertEquals(1, result.size());
        assertEquals("Recital prueba", result.get(0).getName());
        assertEquals(venueId, result.get(0).getVenue().getId());
    }

    @Test
    void getRecitalById_shouldMapPartialEntity_whenPriceOrDateAreMissing() {
        Long recitalId = 12L;
        VenueEntity venueEntity = buildVenueEntity(5L);
        RecitalEntity recitalEntity = new RecitalEntity(recitalId, "Recital parcial", venueEntity, List.of("Banda A"));
        recitalEntity.setType(RecitalType.FESTIVAL);
        recitalEntity.setStatus(RecitalStatus.POSTPONED);
        recitalEntity.setRecitalLink("https://example.com/parcial");
        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));

        Recital result = service.getRecitalById(recitalId);

        assertEquals(recitalId, result.getId());
        assertEquals("Recital parcial", result.getName());
        assertEquals(0, result.getMinTicketPrice());
        assertEquals(0, result.getMaxTicketPrice());
        assertEquals(RecitalType.FESTIVAL, result.getType());
        assertEquals(RecitalStatus.POSTPONED, result.getStatus());
    }

    @Test
    void getRecitalById_shouldNormalizeMissingMaxPriceAndEndDate_whenOnlyStartAndMinExist() {
        Long recitalId = 13L;
        VenueEntity venueEntity = buildVenueEntity(5L);
        RecitalEntity recitalEntity = new RecitalEntity(recitalId, "Recital normalizado", venueEntity, List.of("Banda A"));
        recitalEntity.setType(RecitalType.NATIONAL);
        recitalEntity.setStatus(RecitalStatus.UPCOMING);
        recitalEntity.setMinTicketPrice(5500);
        recitalEntity.setMaxTicketPrice(null);
        recitalEntity.setStartDate(LocalDate.of(2028, 5, 10));
        recitalEntity.setEndDate(null);
        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));

        Recital result = service.getRecitalById(recitalId);

        assertEquals(5500, result.getMinTicketPrice());
        assertEquals(5500, result.getMaxTicketPrice());
        assertEquals(LocalDate.of(2028, 5, 10), result.getStartDate());
        assertEquals(LocalDate.of(2028, 5, 10), result.getEndDate());
    }

    @Test
    void getRecitalById_shouldMapAsPartial_whenStartDateIsNullButMinPriceExists() {
        Long recitalId = 14L;
        VenueEntity venueEntity = buildVenueEntity(5L);
        RecitalEntity recitalEntity = new RecitalEntity(recitalId, "Recital start null", venueEntity, List.of("Banda A"));
        recitalEntity.setType(RecitalType.NATIONAL);
        recitalEntity.setStatus(RecitalStatus.UPCOMING);
        recitalEntity.setMinTicketPrice(5500);
        recitalEntity.setStartDate(null);
        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));

        Recital result = service.getRecitalById(recitalId);

        assertEquals(0, result.getMinTicketPrice());
        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
    }

    @Test
    void getRecitalById_shouldReturnRecital_whenExists() {
        Long recitalId = 11L;
        VenueEntity venueEntity = buildVenueEntity(5L);
        RecitalEntity recitalEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2027, 3, 2), LocalDate.of(2027, 3, 3), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));

        Recital result = service.getRecitalById(recitalId);

        assertEquals(recitalId, result.getId());
        assertEquals("Recital prueba", result.getName());
        assertEquals(5000, result.getMinTicketPrice());
    }

    @Test
    void updateRecital_shouldSetNullLink_whenBlankLinkIsSent() {
        Long recitalId = 32L;
        Long venueId = 3L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity existingEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2027, 4, 10), LocalDate.of(2027, 4, 12), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(existingEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.updateRecital(
                recitalId,
                null,
                null,
                null,
                null,
                null,
                null,
                "   "
        );

        assertNull(result.getRecitalLink());
    }

    @Test
    void updateRecital_shouldSkipPriceUpdateWhenOnlyMinPriceIsProvidedAndValidatorAllowsIt() {
        RecitalBusinessValidator mockedValidator = mock(RecitalBusinessValidator.class);
        RecitalServiceImpl serviceWithMockedValidator = new RecitalServiceImpl(recitalJpaRepository, venueJpaRepository, mockedValidator);
        Long recitalId = 132L;
        Long venueId = 3L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity existingEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2028, 4, 10), LocalDate.of(2028, 4, 12), RecitalStatus.UPCOMING);
        doNothing().when(mockedValidator).validateUpdateInput(any(), any(), any(), any(), any(), any(), any(), any());

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(existingEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = serviceWithMockedValidator.updateRecital(
                recitalId,
                null,
                9999.0,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(5000, result.getMinTicketPrice());
        assertEquals(15000, result.getMaxTicketPrice());
    }

    @Test
    void updateRecital_shouldSkipDateUpdateWhenOnlyStartDateIsProvidedAndValidatorAllowsIt() {
        RecitalBusinessValidator mockedValidator = mock(RecitalBusinessValidator.class);
        RecitalServiceImpl serviceWithMockedValidator = new RecitalServiceImpl(recitalJpaRepository, venueJpaRepository, mockedValidator);
        Long recitalId = 133L;
        Long venueId = 3L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity existingEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2028, 4, 10), LocalDate.of(2028, 4, 12), RecitalStatus.UPCOMING);
        doNothing().when(mockedValidator).validateUpdateInput(any(), any(), any(), any(), any(), any(), any(), any());

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(existingEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = serviceWithMockedValidator.updateRecital(
                recitalId,
                null,
                null,
                null,
                "2029-01-01",
                null,
                null,
                null
        );

        assertEquals(LocalDate.of(2028, 4, 10), result.getStartDate());
        assertEquals(LocalDate.of(2028, 4, 12), result.getEndDate());
    }

    @Test
    void updateRecitalVenue_shouldMoveRecitalToNewVenue() {
        Long recitalId = 41L;
        Long oldVenueId = 8L;
        Long newVenueId = 9L;
        VenueEntity oldVenue = buildVenueEntity(oldVenueId);
        VenueEntity newVenue = new VenueEntity(newVenueId, "Movistar Arena", "Tupper 1941", oldVenue.getCity());
        RecitalEntity recitalEntity = buildRecitalEntity(recitalId, oldVenue, LocalDate.of(2028, 1, 10), LocalDate.of(2028, 1, 11), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));
        when(venueJpaRepository.findById(newVenueId)).thenReturn(Optional.of(newVenue));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.updateRecitalVenue(recitalId, newVenueId);

        assertEquals(newVenueId, result.getVenue().getId());
        assertEquals("Movistar Arena", result.getVenue().getName());
    }

    @Test
    void addRecitalBands_shouldAppendBands() {
        Long recitalId = 42L;
        Long venueId = 10L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2028, 2, 10), LocalDate.of(2028, 2, 11), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.addRecitalBands(recitalId, List.of("Banda C"));

        assertEquals(List.of("Banda A", "Banda B", "Banda C"), result.getBands());
    }

    @Test
    void addRecitalBands_shouldPersistWithoutOptionalStateWhenEntityHasNoPriceOrDate() {
        Long recitalId = 142L;
        Long venueId = 10L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = new RecitalEntity(recitalId, "Recital parcial", venueEntity, List.of("Banda A"));
        recitalEntity.setType(RecitalType.NATIONAL);
        recitalEntity.setStatus(RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.addRecitalBands(recitalId, List.of("Banda B"));

        assertEquals(List.of("Banda A", "Banda B"), result.getBands());
        assertEquals(0, result.getMinTicketPrice());
        assertEquals(0, result.getMaxTicketPrice());
        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
    }

    @Test
    void removeRecitalBands_shouldRemoveBands() {
        Long recitalId = 43L;
        Long venueId = 10L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2028, 2, 10), LocalDate.of(2028, 2, 11), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.removeRecitalBands(recitalId, List.of("Banda A"));

        assertEquals(List.of("Banda B"), result.getBands());
    }

    @Test
    void updateRecitalTicketPriceRange_shouldUpdatePrices() {
        Long recitalId = 44L;
        Long venueId = 10L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2028, 2, 10), LocalDate.of(2028, 2, 11), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.updateRecitalTicketPriceRange(recitalId, 8000.0, 12000.0);

        assertEquals(8000, result.getMinTicketPrice());
        assertEquals(12000, result.getMaxTicketPrice());
    }

    @Test
    void updateRecitalDateRange_shouldUpdateDates() {
        Long recitalId = 45L;
        Long venueId = 10L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(recitalId, venueEntity, LocalDate.of(2028, 2, 10), LocalDate.of(2028, 2, 11), RecitalStatus.UPCOMING);

        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));
        when(venueJpaRepository.findById(venueId)).thenReturn(Optional.of(venueEntity));
        when(recitalJpaRepository.save(any(RecitalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recital result = service.updateRecitalDateRange(recitalId, "2028-06-01", "2028-06-03");

        assertEquals(LocalDate.of(2028, 6, 1), result.getStartDate());
        assertEquals(LocalDate.of(2028, 6, 3), result.getEndDate());
    }

    @Test
    void deleteRecital_shouldDeleteEntity_whenRecitalExists() {
        Long recitalId = 46L;
        RecitalEntity recitalEntity = buildRecitalEntity(recitalId, buildVenueEntity(10L), LocalDate.of(2028, 2, 10), LocalDate.of(2028, 2, 11), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findById(recitalId)).thenReturn(Optional.of(recitalEntity));

        service.deleteRecital(recitalId);

        verify(recitalJpaRepository).delete(recitalEntity);
    }

    @Test
    void getRecitals_withInvalidVenueIdFilter_shouldThrowBusinessValidationException() {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                null,
                null,
                0L,
                null,
                null,
                null,
                null
        );

        assertThrows(BusinessValidationException.class, () -> service.getRecitals(criteria));
    }

    @Test
    void getRecitals_withInvalidPriceWindow_shouldThrowBusinessValidationException() {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                null,
                null,
                null,
                20000,
                10000,
                null,
                null
        );

        assertThrows(BusinessValidationException.class, () -> service.getRecitals(criteria));
    }

    @Test
    void getRecitals_withInvalidMinPriceFilter_shouldThrowBusinessValidationException() {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                null,
                null,
                null,
                0,
                null,
                null,
                null
        );

        assertThrows(BusinessValidationException.class, () -> service.getRecitals(criteria));
    }

    @Test
    void getRecitals_withInvalidMaxPriceFilter_shouldThrowBusinessValidationException() {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                null,
                null,
                null,
                null,
                0,
                null,
                null
        );

        assertThrows(BusinessValidationException.class, () -> service.getRecitals(criteria));
    }

    @Test
    void getRecitals_withInvalidDateWindow_shouldThrowBusinessValidationException() {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                null,
                null,
                null,
                null,
                null,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 1)
        );

        assertThrows(BusinessValidationException.class, () -> service.getRecitals(criteria));
    }

    @Test
    void getRecitals_withValidComparableRanges_shouldPassValidationPath() {
        Long venueId = 500L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(501L, venueEntity, LocalDate.of(2028, 7, 10), LocalDate.of(2028, 7, 10), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findAll(any(Specification.class))).thenReturn(List.of(recitalEntity));

        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                RecitalType.NATIONAL,
                RecitalStatus.UPCOMING,
                venueId,
                5000,
                5000,
                LocalDate.of(2028, 7, 10),
                LocalDate.of(2028, 7, 10)
        );

        List<Recital> result = service.getRecitals(criteria);

        assertEquals(1, result.size());
        assertEquals(501L, result.get(0).getId());
    }

    @Test
    void getRecitals_withMinPriceAndNoMaxPrice_shouldPassValidationPath() {
        Long venueId = 510L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(511L, venueEntity, LocalDate.of(2028, 7, 10), LocalDate.of(2028, 7, 11), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findAll(any(Specification.class))).thenReturn(List.of(recitalEntity));

        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                null,
                null,
                null,
                5000,
                null,
                null,
                null
        );

        List<Recital> result = service.getRecitals(criteria);

        assertEquals(1, result.size());
        assertEquals(511L, result.get(0).getId());
    }

    @Test
    void getRecitals_withOnlyStartDateFrom_shouldPassValidationPath() {
        Long venueId = 520L;
        VenueEntity venueEntity = buildVenueEntity(venueId);
        RecitalEntity recitalEntity = buildRecitalEntity(521L, venueEntity, LocalDate.of(2028, 7, 10), LocalDate.of(2028, 7, 11), RecitalStatus.UPCOMING);
        when(recitalJpaRepository.findAll(any(Specification.class))).thenReturn(List.of(recitalEntity));

        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                null,
                null,
                null,
                null,
                null,
                LocalDate.of(2028, 7, 1),
                null
        );

        List<Recital> result = service.getRecitals(criteria);

        assertEquals(1, result.size());
        assertEquals(521L, result.get(0).getId());
    }

    @Test
    void getRecitalById_shouldThrowResourceNotFound_whenIdIsNull() {
        assertThrows(ResourceNotFoundException.class, () -> service.getRecitalById(null));
    }

    @Test
    void getRecitalById_shouldThrowResourceNotFound_whenIdIsNegative() {
        assertThrows(ResourceNotFoundException.class, () -> service.getRecitalById(-1L));
    }

    @Test
    void getRecitalById_shouldThrowResourceNotFound_whenRecitalDoesNotExist() {
        when(recitalJpaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getRecitalById(999L));
    }

    private VenueEntity buildVenueEntity(Long venueId) {
        RegionEntity regionEntity = new RegionEntity(1L, "Metropolitana");
        CityEntity cityEntity = new CityEntity(10L, "Santiago", regionEntity);
        return new VenueEntity(venueId, "Teatro Municipal", "Av. Siempre Viva 123", cityEntity);
    }

    private RecitalEntity buildRecitalEntity(Long recitalId, VenueEntity venueEntity, LocalDate startDate, LocalDate endDate, RecitalStatus status) {
        RecitalEntity recitalEntity = new RecitalEntity(recitalId, "Recital prueba", venueEntity, List.of("Banda A", "Banda B"));
        recitalEntity.setType(RecitalType.NATIONAL);
        recitalEntity.setStatus(status);
        recitalEntity.setMinTicketPrice(5000);
        recitalEntity.setMaxTicketPrice(15000);
        recitalEntity.setStartDate(startDate);
        recitalEntity.setEndDate(endDate);
        recitalEntity.setRecitalLink("https://example.com/recital");
        return recitalEntity;
    }
}
