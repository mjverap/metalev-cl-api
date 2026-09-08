package cl.mjvera.metalevcl.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiResponse", description = "Standard response envelope for API operations")
public record ApiResponse(
    @Schema(description = "HTTP status code", example = "200") int status,
    @Schema(description = "Human-readable message", example = "Operation completed successfully") String mensaje,
    @Schema(description = "Optional entity name associated with the response", example = "recital") String nombre,
    @Schema(description = "Timestamp when the response was produced", example = "2026-09-08T01:06:59") LocalDateTime timestamp
) {
    public static ApiResponse ok(String mensaje, String nombre) {
        return new ApiResponse(200, mensaje, nombre, LocalDateTime.now());
    }

    public static ApiResponse ok(String mensaje) {
        return new ApiResponse(200, mensaje, null, LocalDateTime.now());
    }

    public static ApiResponse error(int status, String mensaje) {
        return new ApiResponse(status, mensaje, null, LocalDateTime.now());
    }
}
