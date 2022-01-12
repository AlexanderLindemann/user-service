package com.nft.platform.initializer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

@Slf4j
public class WiremockServerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String WIREMOCK_SERVER_BEAN_NAME = "wireMockServer";

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        var wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
        wireMockServer.start();

        configurableApplicationContext.getBeanFactory().registerSingleton(WIREMOCK_SERVER_BEAN_NAME, wireMockServer);

        configurableApplicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        var wiremockHost = "http://localhost:" + wireMockServer.port();
        TestPropertyValues.of(
                "nft.tests.mock.url=" + wiremockHost
        ).applyTo(configurableApplicationContext.getEnvironment());

        log.info("Initialized wiremock at {}", wiremockHost);
    }
}
