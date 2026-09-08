package cl.mjvera.metalevcl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class ApplicationBootstrapTest {

    @Test
    void main_shouldDelegateToSpringApplicationRun() {
        try (var springApplication = mockStatic(SpringApplication.class)) {
            springApplication.when(() -> SpringApplication.run(eq(MetalevclApplication.class), any(String[].class)))
                    .thenReturn(mock(org.springframework.context.ConfigurableApplicationContext.class));

            MetalevclApplication.main(new String[]{"--spring.main.banner-mode=off"});

            springApplication.verify(() -> SpringApplication.run(eq(MetalevclApplication.class), any(String[].class)));
        }
    }

    @Test
    void servletInitializer_shouldConfigureApplicationSources() {
        ServletInitializer initializer = new ServletInitializer();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        SpringApplicationBuilder configured = initializer.configure(builder);

        assertNotNull(configured);
    }
}

