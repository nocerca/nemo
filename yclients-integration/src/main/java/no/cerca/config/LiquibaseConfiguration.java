package no.cerca.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Created by jadae on 05.03.2025
 */
@Configuration
public class LiquibaseConfiguration {

    @Value("${spring.liquibase.change-log}")
    private String changeLog;

    @Bean
    @Profile("!test")
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        return liquibase;
    }

    @Bean
    @Profile("test")
    public SpringLiquibase liquibaseStub() {
        return new SpringLiquibase() {
            @Override
            public void afterPropertiesSet() {
            }
        };
    }
}
