package integration;

import integration.configuration.PostgresContainer;
import no.cerca.MainApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

/**
 * Created by jadae on 03.04.2025
 */
@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.properties"})
@ContextConfiguration(initializers = {SpringBootApplicationTest.Initializer.class})
public abstract class SpringBootApplicationTest {

    static {
        PostgresContainer.getInstance().start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {

            var container = PostgresContainer.getInstance();

            var delegate = new JdbcDatabaseDelegate(container, "");
            ScriptUtils.runInitScript(delegate, "database/data/create_sample_entities.sql");

            TestPropertyValues.of(
                    "spring.datasource.url=" + container.getJdbcUrl(),
                    "spring.datasource.username=" + container.getUsername(),
                    "spring.datasource.password=" + container.getPassword()
            ).applyTo(context.getEnvironment());
        }
    }
}
