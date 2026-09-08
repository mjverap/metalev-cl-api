package cl.mjvera.metalevcl.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev")
public class OpenApiConfig {

    @Bean
    public OpenAPI metalevApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Metal Events CL API")
                        .version("1.0.0")
                        .description("API REST para gestión de recitales y venues de Chile."))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")));
    }
}
