package cl.mjvera.metalevcl.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "VenueRequest", description = "Payload used to create a venue")
public record VenueRequestDto(
        @Schema(description = "Venue name", example = "Teatro Ex Mundo Mágico")
        @NotBlank(message = "name is required")
        @Size(min = 1, max = 100, message = "name length must be between 1 and 100")
        String name,
        @Schema(description = "Street address", example = "Av. Gral. Óscar Bonilla 6100")
        @NotBlank(message = "street is required")
        @Size(max = 150, message = "street length must be less than or equal to 150")
        String street,
        @Schema(description = "Identifier of the city bound to the venue", example = "310")
        @NotNull(message = "cityId is required")
        @Positive(message = "cityId must be greater than 0")
        Long cityId
) {
}
