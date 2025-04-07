package integration.configuration;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Created by jadae on 03.04.2025
 */
public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

    private static final String DOCKER_IMAGE = "postgres:13.1-alpine";
    private static final PostgresContainer INSTANCE = new PostgresContainer();

    private PostgresContainer() {
        super(DOCKER_IMAGE);
        withDatabaseName("test");
        withUsername("test");
        withPassword("test");
        withInitScript("database/schema/integration-schema.sql");
    }

    public static PostgresContainer getInstance() {
        return INSTANCE;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            super.start();
        }
    }
}
