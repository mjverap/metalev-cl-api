package cl.mjvera.metalevcl.infrastructure.web.dto;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "RecitalResponse", description = "Response model for a recital")
public record RecitalResponseDto(
        @Schema(description = "Recital identifier", example = "1") Long id,
        @Schema(description = "Recital name", example = "Fear Factory en Chile") String name,
        @Schema(description = "Minimum ticket price", example = "15000") Integer minPrice,
        @Schema(description = "Maximum ticket price", example = "35000") Integer maxPrice,
        @Schema(description = "Start date", example = "2026-11-18") LocalDate startDate,
        @Schema(description = "End date", example = "2026-11-18") LocalDate endDate,
        @Schema(description = "Associated venue") VenueResponseDto venue,
        @Schema(description = "Bands participating in the recital") List<String> bands,
        @Schema(description = "Type of recital", example = "INTERNATIONAL") RecitalType type,
        @Schema(description = "Current recital status", example = "UPCOMING") RecitalStatus status,
        @Schema(description = "External URL", example = "https://example.com/recital") String recitalLink
) {
}
