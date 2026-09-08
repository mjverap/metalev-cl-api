package cl.mjvera.metalevcl.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ApiResponseTest {

    @Test
    void constructor_shouldAssignAllFields() {
        LocalDateTime timestamp = LocalDateTime.now();
        ApiResponse response = new ApiResponse(201, "Creado", "Recital X", timestamp);

        assertEquals(201, response.status());
        assertEquals("Creado", response.mensaje());
        assertEquals("Recital X", response.nombre());
        assertEquals(timestamp, response.timestamp());
    }

    @Test
    void okWithMessageAndName_shouldBuildSuccessResponse() {
        ApiResponse response = ApiResponse.ok("Operación exitosa", "Recital X");

        assertEquals(200, response.status());
        assertEquals("Operación exitosa", response.mensaje());
        assertEquals("Recital X", response.nombre());
        assertNotNull(response.timestamp());
    }

    @Test
    void okWithMessage_shouldBuildSuccessResponseWithoutName() {
        ApiResponse response = ApiResponse.ok("Operación exitosa");

        assertEquals(200, response.status());
        assertEquals("Operación exitosa", response.mensaje());
        assertNull(response.nombre());
        assertNotNull(response.timestamp());
    }

    @Test
    void error_shouldBuildErrorResponseWithoutName() {
        ApiResponse response = ApiResponse.error(404, "No encontrado");

        assertEquals(404, response.status());
        assertEquals("No encontrado", response.mensaje());
        assertNull(response.nombre());
        assertNotNull(response.timestamp());
    }
}
