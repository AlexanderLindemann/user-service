package com.nft.platform.util.initializer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final DockerImageName postgres = DockerImageName.parse("postgres:11-alpine")
            .asCompatibleSubstituteFor("postgres");

    public static final PostgreSQLContainer dbContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres)
            .withDatabaseName("database")
            .withUsername("user")
            .withPassword("password")
            .withReuse(false);

    static {
        dbContainer.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + dbContainer.getJdbcUrl(),
                "spring.datasource.username=" + dbContainer.getUsername(),
                "spring.datasource.password=" + dbContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
