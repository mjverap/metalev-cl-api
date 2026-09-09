package cl.mjvera.metalevcl.infrastructure.web.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CorsConfigTest {

    @Test
    public void shouldConfigureCorsMapping(){
        CorsConfig corsConfig = new CorsConfig();
        CorsRegistry registry = new CorsRegistry();
        corsConfig.addCorsMappings(registry);

        assertNotNull(registry);
    }
}
