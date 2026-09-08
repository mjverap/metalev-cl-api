package cl.mjvera.metalevcl.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VenueResponse", description = "Response model for a venue")
public record VenueResponseDto(
        @Schema(description = "Venue identifier", example = "17") Long id,
        @Schema(description = "Venue name", example = "Teatro Coliseo") String name,
        @Schema(description = "Street address", example = "Nataniel Cox 59") String street,
        @Schema(description = "City identifier", example = "342") Long cityId,
        @Schema(description = "City name", example = "Santiago") String city,
        @Schema(description = "Region name", example = "Región Metropolitana de Santiago") String region
) {
}
