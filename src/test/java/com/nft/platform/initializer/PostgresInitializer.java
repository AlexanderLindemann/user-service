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

    private static DockerImageName postgres = TestUtil.getTesContainersDockerImage("postgres:11-alpine").asCompatibleSubstituteFor("postgres");

    public static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>(postgres)
            .withDatabaseName("template")
            .withUsername("template")
            .withPassword("template")
            .withReuse(false)
            ;

    static {
        log.info("Starting postgres testcontainers initializer...");
        try {
            dbContainer.start();
        } catch (Throwable e) {
            throw new TestInitializationException("Exception occurred while initialization postgres test container!", e);
        }
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
