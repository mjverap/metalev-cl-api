package cl.mjvera.metalevcl.infrastructure.web.controller;

import cl.mjvera.metalevcl.application.service.VenueService;
import cl.mjvera.metalevcl.domain.model.Venue;
import cl.mjvera.metalevcl.infrastructure.web.dto.VenueAddressUpdateRequestDto;
import cl.mjvera.metalevcl.infrastructure.web.dto.VenueRequestDto;
import cl.mjvera.metalevcl.infrastructure.web.dto.VenueResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
@Tag(name = "Venues", description = "Operations for managing venues")
public class VenueController {
    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @Operation(
            summary = "Create venue",
            description = "Creates a venue and associates it with a valid city identifier.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Venue created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PostMapping
    public ResponseEntity<VenueResponseDto> createVenue(@Valid @RequestBody VenueRequestDto request) {
        Venue createdVenue = venueService.createVenue(
                request.name(),
                request.street(),
                request.cityId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(createdVenue));
    }

    @Operation(
            summary = "List venues",
            description = "Returns venues filtered by region and city.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Venues found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponseDto.class)))
            }
    )
    @GetMapping
    public List<VenueResponseDto> getVenues(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String city
    ) {
        return venueService.getVenues(region, city).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Operation(
            summary = "Update venue address",
            description = "Updates only the address fields of a venue without changing its name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Venue address updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Venue not found")
            }
    )
    @PatchMapping("/{id}/address")
    public ResponseEntity<VenueResponseDto> updateVenueAddress(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody VenueAddressUpdateRequestDto request
    ) {
        Venue venue = venueService.updateVenueAddress(id, request.street(), request.cityId());
        return ResponseEntity.ok(toResponseDto(venue));
    }

    @Operation(
            summary = "Delete venue",
            description = "Deletes a venue when it has no associated recitals.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Venue deleted"),
                    @ApiResponse(responseCode = "404", description = "Venue not found"),
                    @ApiResponse(responseCode = "409", description = "Venue cannot be deleted because it has recitals")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable(name = "id") Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    private VenueResponseDto toResponseDto(Venue venue) {
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
