package cl.mjvera.metalevcl.infrastructure.web.controller;

import cl.mjvera.metalevcl.application.service.RecitalService;
import cl.mjvera.metalevcl.application.service.VenueService;
import cl.mjvera.metalevcl.domain.model.City;
import cl.mjvera.metalevcl.domain.model.Recital;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import cl.mjvera.metalevcl.domain.model.Region;
import cl.mjvera.metalevcl.domain.model.Venue;
import cl.mjvera.metalevcl.domain.valueobject.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock
    private RecitalService recitalService;

    @Mock
    private VenueService venueService;

    @Test
    void homeController_shouldReturnHealthcheck() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HomeController()).build();

        mockMvc.perform(get("/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void recitalController_createRecital_shouldReturnCreatedResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new RecitalController(recitalService)).build();
        Venue venue = buildVenue(1L, "Teatro Municipal", 10L, "Santiago", 1L, "Metropolitana");
        Recital recital = new Recital(
                99L,
                "Festival de Verano",
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

        when(recitalService.createRecital(
                eq("Festival de Verano"),
                eq(5000.0),
                eq(12000.0),
                eq("2026-05-01"),
                eq("2026-05-03"),
                eq(1L),
                eq(List.of("Banda A", "Banda B")),
                eq(RecitalType.NATIONAL),
                eq(RecitalStatus.UPCOMING),
                eq("https://example.com/festival")
        )).thenReturn(recital);

        mockMvc.perform(post("/api/v1/recitals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Festival de Verano",
                                  "minPrice": 5000.0,
                                  "maxPrice": 12000.0,
                                  "startDate": "2026-05-01",
                                  "endDate": "2026-05-03",
                                  "venueId": 1,
                                  "bands": ["Banda A", "Banda B"],
                                  "type": "NACIONAL",
                                  "status": "UPCOMING",
                                  "recitalLink": "https://example.com/festival"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.name").value("Festival de Verano"))
                .andExpect(jsonPath("$.venue.name").value("Teatro Municipal"))
                .andExpect(jsonPath("$.bands[0]").value("Banda A"));
    }

    @Test
    void recitalController_getRecitals_shouldMapFiltersAndResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new RecitalController(recitalService)).build();
        Venue venue = buildVenue(2L, "Auditorio Central", 12L, "Concepción", 2L, "Biobío");
        Recital recital = new Recital(
                21L,
                "Recital X",
                venue,
                List.of("Grupo 1"),
                7000,
                9000,
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 12),
                RecitalType.INTERNATIONAL,
                RecitalStatus.UPCOMING,
                "https://example.com/x"
        );

        when(recitalService.getRecitals(any())).thenReturn(List.of(recital));

        mockMvc.perform(get("/api/v1/recitals")
                        .param("type", "INTERNACIONAL")
                        .param("status", "UPCOMING")
                        .param("venueId", "2")
                        .param("minPrice", "7000")
                        .param("maxPrice", "9000")
                        .param("startDateFrom", "2026-08-01")
                        .param("endDateTo", "2026-08-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(21))
                .andExpect(jsonPath("$[0].name").value("Recital X"))
                .andExpect(jsonPath("$[0].type").value("INTERNACIONAL"))
                .andExpect(jsonPath("$[0].venue.city").value("Concepción"));
    }

    @Test
    void recitalController_getRecitalById_shouldReturnRecital() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new RecitalController(recitalService)).build();
        Venue venue = buildVenue(7L, "Teatro Norte", 17L, "Antofagasta", 3L, "Antofagasta");
        Recital recital = new Recital(
                50L,
                "Recital detalle",
                venue,
                List.of("Banda Z"),
                10000,
                20000,
                LocalDate.of(2027, 9, 10),
                LocalDate.of(2027, 9, 11),
                RecitalType.NATIONAL,
                RecitalStatus.SOLD_OUT,
                "https://example.com/detalle"
        );
        when(recitalService.getRecitalById(50L)).thenReturn(recital);

        mockMvc.perform(get("/api/v1/recitals/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.name").value("Recital detalle"))
                .andExpect(jsonPath("$.venue.name").value("Teatro Norte"))
                .andExpect(jsonPath("$.status").value("SOLD_OUT"))
                .andExpect(jsonPath("$.bands[0]").value("Banda Z"));
    }

    @Test
    void recitalController_updateRecital_shouldReturnUpdatedRecital() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new RecitalController(recitalService)).build();
        Venue venue = buildVenue(8L, "Teatro Sur", 18L, "Valdivia", 4L, "Los Ríos");
        Recital updatedRecital = new Recital(
                55L,
                "Recital actualizado",
                venue,
                List.of("Banda A", "Banda B"),
                11000,
                22000,
                LocalDate.of(2027, 11, 1),
                LocalDate.of(2027, 11, 3),
                RecitalType.INTERNATIONAL,
                RecitalStatus.POSTPONED,
                "https://example.com/actualizado"
        );

        when(recitalService.updateRecital(
                eq(55L),
                eq(List.of("Banda A", "Banda B")),
                eq(11000.0),
                eq(22000.0),
                eq("2027-11-01"),
                eq("2027-11-03"),
                eq(RecitalStatus.POSTPONED),
                eq("https://example.com/actualizado")
        )).thenReturn(updatedRecital);

        mockMvc.perform(patch("/api/v1/recitals/55")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bands": ["Banda A", "Banda B"],
                                  "minPrice": 11000.0,
                                  "maxPrice": 22000.0,
                                  "startDate": "2027-11-01",
                                  "endDate": "2027-11-03",
                                  "status": "POSTPONED",
                                  "recitalLink": "https://example.com/actualizado"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55))
                .andExpect(jsonPath("$.name").value("Recital actualizado"))
                .andExpect(jsonPath("$.type").value("INTERNACIONAL"))
                .andExpect(jsonPath("$.status").value("POSTPONED"))
                .andExpect(jsonPath("$.venue.name").value("Teatro Sur"));
    }

    @Test
    void recitalController_deleteRecital_shouldReturnNoContent() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new RecitalController(recitalService)).build();
        doNothing().when(recitalService).deleteRecital(7L);

        mockMvc.perform(delete("/api/v1/recitals/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void venueController_createVenue_shouldReturnCreatedResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new VenueController(venueService)).build();
        Venue venue = buildVenue(3L, "Teatro de la Plaza", 15L, "Viña del Mar", 4L, "Valparaíso");

        when(venueService.createVenue("Teatro de la Plaza", "Calle Principal 555", 15L)).thenReturn(venue);

        mockMvc.perform(post("/api/v1/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Teatro de la Plaza",
                                  "street": "Calle Principal 555",
                                  "cityId": 15
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Teatro de la Plaza"))
                .andExpect(jsonPath("$.city").value("Viña del Mar"))
                .andExpect(jsonPath("$.region").value("Valparaíso"));
    }

    @Test
    void venueController_getVenues_shouldReturnFilteredList() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new VenueController(venueService)).build();
        Venue venue = buildVenue(4L, "Club Arena", 20L, "Puerto Montt", 5L, "Los Lagos");
        when(venueService.getVenues("Los Lagos", "Puerto Montt")).thenReturn(List.of(venue));

        mockMvc.perform(get("/api/v1/venues")
                        .param("region", "Los Lagos")
                        .param("city", "Puerto Montt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].name").value("Club Arena"))
                .andExpect(jsonPath("$[0].city").value("Puerto Montt"))
                .andExpect(jsonPath("$[0].region").value("Los Lagos"));
    }

    @Test
    void venueController_updateVenueAddress_shouldReturnOkResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new VenueController(venueService)).build();
        Region region = new Region(6L, "Araucanía");
        City city = new City(25L, "Temuco", region);
        Venue venue = new Venue(5L, "Nuevo Teatro", new Address("Avenida Nueva 120", city));
        when(venueService.updateVenueAddress(5L, "Avenida Nueva 120", 25L)).thenReturn(venue);

        mockMvc.perform(patch("/api/v1/venues/5/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "street": "Avenida Nueva 120",
                                  "cityId": 25
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.street").value("Avenida Nueva 120"))
                .andExpect(jsonPath("$.city").value("Temuco"));
    }

    @Test
    void venueController_deleteVenue_shouldReturnNoContent() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new VenueController(venueService)).build();
        doNothing().when(venueService).deleteVenue(9L);

        mockMvc.perform(delete("/api/v1/venues/9"))
                .andExpect(status().isNoContent());
    }

    private Venue buildVenue(Long venueId, String venueName, Long cityId, String cityName, Long regionId, String regionName) {
        Region region = new Region(regionId, regionName);
        City city = new City(cityId, cityName, region);
        return new Venue(venueId, venueName, new Address("Calle Principal 123", city));
    }
}
