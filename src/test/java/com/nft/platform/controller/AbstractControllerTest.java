package com.nft.platform.controller;

import com.nft.platform.initializer.PostgresInitializer;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.requestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = PostgresInitializer.class)
public abstract class AbstractControllerTest {
    @LocalServerPort
    protected int port;


    @BeforeEach
    void setUp() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath(getBasePath())
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setPort(port)
                .build();
    }

    abstract String getBasePath();

}
