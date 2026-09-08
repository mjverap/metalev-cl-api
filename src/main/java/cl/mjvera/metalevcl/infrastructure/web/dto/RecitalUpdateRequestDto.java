package cl.mjvera.metalevcl.infrastructure.web.dto;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Schema(name = "RecitalUpdateRequest", description = "Payload used to partially update a recital")
public record RecitalUpdateRequestDto(
        @Schema(description = "Updated list of band names")
        List<@NotBlank(message = "band name cannot be blank")
             @Size(min = 1, max = 100, message = "band name length must be between 1 and 100") String> bands,
        @Schema(description = "Updated minimum ticket price", example = "18000")
        Double minPrice,
        @Schema(description = "Updated maximum ticket price", example = "400000")
        Double maxPrice,
        @Schema(description = "Updated start date in ISO format", example = "2026-11-19")
        String startDate,
        @Schema(description = "Updated end date in ISO format", example = "2026-11-21")
        String endDate,
        @Schema(description = "Updated recital status", example = "POSTERGADO")
        RecitalStatus status,
        @Schema(description = "Updated external URL", example = "https://example.com/update")
        @URL(message = "recitalLink must be a valid URL")
        String recitalLink
) {
}
