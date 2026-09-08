package cl.mjvera.metalevcl.infrastructure.web.dto;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Schema(name = "RecitalRequest", description = "Payload used to create a recital")
public record RecitalRequestDto(
        @Schema(description = "Recital name", example = "MetalFest Chile 2027")
        @NotBlank(message = "name is required")
        @Size(min = 1, max = 100, message = "name length must be between 1 and 100")
        String name,
        @Schema(description = "Minimum ticket price", example = "15000")
        @NotNull(message = "minPrice is required")
        @Positive(message = "minPrice must be greater than 0")
        Double minPrice,
        @Schema(description = "Maximum ticket price", example = "350000")
        @DecimalMax(value = "10000000", message = "maxPrice must be less than or equal to 10000000")
        Double maxPrice,
        @Schema(description = "Start date in ISO format", example = "2026-11-18")
        @NotBlank(message = "startDate is required")
        String startDate,
        @Schema(description = "End date in ISO format", example = "2026-11-20")
        String endDate,
        @Schema(description = "Venue identifier", example = "12")
        @NotNull(message = "venueId is required")
        @Positive(message = "venueId must be greater than 0")
        Long venueId,
        @Schema(description = "List of band names participating in the recital")
        @NotNull(message = "bands is required")
        List<@NotBlank(message = "band name cannot be blank")
             @Size(min = 1, max = 100, message = "band name length must be between 1 and 100") String> bands,
        @Schema(description = "Type of recital", example = "FESTIVAL")
        RecitalType type,
        @Schema(description = "Current status of the recital", example = "UPCOMING")
        RecitalStatus status,
        @Schema(description = "External URL for the recital", example = "https://example.com/event")
        @URL(message = "recitalLink must be a valid URL")
        String recitalLink
) {
}
