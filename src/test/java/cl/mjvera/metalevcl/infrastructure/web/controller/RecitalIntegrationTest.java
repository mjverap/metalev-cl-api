package cl.mjvera.metalevcl.infrastructure.web.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import cl.mjvera.metalevcl.infrastructure.persistence.CityEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.RecitalEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.RegionEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.VenueEntity;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.CityJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.RecitalJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.RegionJpaRepository;
import cl.mjvera.metalevcl.infrastructure.persistence.repository.VenueJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecitalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecitalJpaRepository recitalJpaRepository;

    @Autowired
    private VenueJpaRepository venueJpaRepository;

    @Autowired
    private CityJpaRepository cityJpaRepository;

    @Autowired
    private RegionJpaRepository regionJpaRepository;

    @Test
    @DisplayName("GET /api/v1/recitals debe retornar la lista de recitales con DTO alineado para el Frontend")
    void shouldReturnAllRecitalsMatchingFrontendContract() throws Exception {
        String uniqueRegionName = "Región " + UUID.randomUUID().toString().substring(0, 6);
        String uniqueCityName = "Ciudad " + UUID.randomUUID().toString().substring(0, 6);
        String uniqueVenueName = "Teatro " + UUID.randomUUID().toString().substring(0, 6);
        String uniqueRecitalName = "Recital " + UUID.randomUUID().toString().substring(0, 6);

        RegionEntity region = regionJpaRepository.save(new RegionEntity(uniqueRegionName));
        CityEntity city = cityJpaRepository.save(new CityEntity(uniqueCityName, region));
        VenueEntity venue = venueJpaRepository.save(new VenueEntity(uniqueVenueName, "Av. Principal 123", city));

        RecitalEntity recital = new RecitalEntity(uniqueRecitalName, venue, java.util.List.of("Banda A", "Banda B"));
        recital.setMinTicketPrice(5000);
        recital.setMaxTicketPrice(15000);
        recital.setStartDate(java.time.LocalDate.of(2026, 5, 10));
        recital.setEndDate(java.time.LocalDate.of(2026, 5, 12));
        recital.setType(RecitalType.NATIONAL);
        recital.setStatus(RecitalStatus.UPCOMING);
        recital.setRecitalLink("https://example.com/festival");
        recitalJpaRepository.save(recital);

        mockMvc.perform(get("/api/v1/recitals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem(uniqueRecitalName)))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].minPrice").value(5000))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].maxPrice").value(15000))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].startDate").value("2026-05-10"))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].endDate").value("2026-05-12"))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].venue.name").value(uniqueVenueName))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].venue.street").value("Av. Principal 123"))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].venue.city").value(uniqueCityName))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].venue.region").value(uniqueRegionName))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].bands[0]").value("Banda A"))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].bands[1]").value("Banda B"))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].type").value("NACIONAL"))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].status").value("UPCOMING"))
                .andExpect(jsonPath("$[?(@.name == '" + uniqueRecitalName + "')].recitalLink").value("https://example.com/festival"));
    }
}
