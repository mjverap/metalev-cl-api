package cl.mjvera.metalevcl.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "VenueAddressUpdateRequest", description = "Payload used to update a venue address")
public record VenueAddressUpdateRequestDto(
        @Schema(description = "Updated street address", example = "Av. Las Condes 2300")
        @NotBlank(message = "street is required")
        @Size(max = 150, message = "street length must be less than or equal to 150")
        String street,
        @Schema(description = "Identifier of the replacement city", example = "10")
        @NotNull(message = "cityId is required")
        @Positive(message = "cityId must be greater than 0")
        Long cityId
) {
}
