package cl.mjvera.metalevcl.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    public DevDataInitializer(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (hasAnyData()) {
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        Resource citiesScript = resourceLoader.getResource("classpath:cities.sql");
        Resource dataScript = resourceLoader.getResource("classpath:data.sql");
        populator.addScript(citiesScript);
        populator.addScript(dataScript);
        populator.execute(jdbcTemplate.getDataSource());
    }

    private boolean hasAnyData() {
        Long regions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM regions", Long.class);
        Long cities = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities", Long.class);
        Long venues = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM venues", Long.class);
        Long recitals = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM recitals", Long.class);
        return regions != null && regions > 0
                || cities != null && cities > 0
                || venues != null && venues > 0
                || recitals != null && recitals > 0;
    }
}
