package com.nft.platform.initializer;

import com.nft.platform.util.TestUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String NAME = "users";
    private static final DockerImageName IMAGE_NAME = TestUtil.getTesContainersDockerImage("postgres:11-alpine").asCompatibleSubstituteFor("postgres");

    public static final PostgreSQLContainer<?> PG_CONTAINER = new PostgreSQLContainer<>(IMAGE_NAME)
        .withDatabaseName(NAME)
        .withUsername(NAME)
        .withPassword(NAME)
        .withReuse(false);

    static {
        log.info("Starting postgres testcontainers initializer...");
        try {
            PG_CONTAINER.start();
        } catch (Throwable e) {
            throw new TestInitializationException("Exception occurred while initialization postgres test container!", e);
        }
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + PG_CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + PG_CONTAINER.getUsername(),
                "spring.datasource.password=" + PG_CONTAINER.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }

}