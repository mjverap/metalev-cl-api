package cl.mjvera.metalevcl.infrastructure.web.controller;

import cl.mjvera.metalevcl.application.service.RecitalSearchCriteria;
import cl.mjvera.metalevcl.application.service.RecitalService;
import cl.mjvera.metalevcl.domain.model.Recital;
import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import cl.mjvera.metalevcl.domain.model.Venue;
import cl.mjvera.metalevcl.infrastructure.web.dto.RecitalRequestDto;
import cl.mjvera.metalevcl.infrastructure.web.dto.RecitalResponseDto;
import cl.mjvera.metalevcl.infrastructure.web.dto.RecitalUpdateRequestDto;
import cl.mjvera.metalevcl.infrastructure.web.dto.VenueResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recitals")
@Tag(name = "Recitals", description = "Operations for managing recitals")
public class RecitalController {

    private final RecitalService recitalService;

    public RecitalController(RecitalService recitalService) {
        this.recitalService = recitalService;
    }

    @Operation(
            summary = "List recitals",
            description = "Returns all recitals with optional filters by type, status, venue, price and date range.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recitals found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecitalResponseDto.class)))
            }
    )
    @GetMapping
    public List<RecitalResponseDto> getRecitals(
            @RequestParam(required = false) RecitalType type,
            @RequestParam(required = false) RecitalStatus status,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateTo
    ) {
        RecitalSearchCriteria criteria = new RecitalSearchCriteria(
                type,
                status,
                venueId,
                minPrice,
                maxPrice,
                startDateFrom,
                endDateTo
        );
        return recitalService.getRecitals(criteria)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Operation(
            summary = "Get recital by id",
            description = "Returns a single recital by identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recital found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecitalResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Recital not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<RecitalResponseDto> getRecitalById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(toResponseDto(recitalService.getRecitalById(id)));
    }

    @Operation(
            summary = "Create recital",
            description = "Creates a new recital and returns the generated entity.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recital created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecitalResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PostMapping
    public ResponseEntity<RecitalResponseDto> createRecital(@Valid @RequestBody RecitalRequestDto request) {
        Recital recital = recitalService.createRecital(
                request.name(),
                request.minPrice(),
                request.maxPrice(),
                request.startDate(),
                request.endDate(),
                request.venueId(),
                request.bands(),
                request.type(),
                request.status(),
                request.recitalLink()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(recital));
    }

    @Operation(
            summary = "Update recital",
            description = "Partially updates a recital with the provided fields.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recital updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecitalResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Recital not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid update payload")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<RecitalResponseDto> updateRecital(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody RecitalUpdateRequestDto request
    ) {
        Recital recital = recitalService.updateRecital(
                id,
                request.bands(),
                request.minPrice(),
                request.maxPrice(),
                request.startDate(),
                request.endDate(),
                request.status(),
                request.recitalLink()
        );
        return ResponseEntity.ok(toResponseDto(recital));
    }

    @Operation(
            summary = "Delete recital",
            description = "Deletes the recital with the specified identifier.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Recital deleted"),
                    @ApiResponse(responseCode = "404", description = "Recital not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecital(@PathVariable(name = "id") Long id) {
        recitalService.deleteRecital(id);
        return ResponseEntity.noContent().build();
    }

    private RecitalResponseDto toResponseDto(Recital recital) {
        return new RecitalResponseDto(
                recital.getId(),
                recital.getName(),
                recital.getMinTicketPrice(),
                recital.getMaxTicketPrice(),
                recital.getStartDate(),
                recital.getEndDate(),
                toVenueResponseDto(recital.getVenue()),
                recital.getBands(),
                recital.getType(),
                recital.getStatus(),
                recital.getRecitalLink()
        );
    }

    private VenueResponseDto toVenueResponseDto(Venue venue) {
        return new VenueResponseDto(
                venue.getId(),
                venue.getName(),
                venue.getAddress().street(),
                venue.getAddress().city().getId(),
                venue.getAddress().city().getName(),
                venue.getAddress().city().getRegion().getName()
        );
    }
}
