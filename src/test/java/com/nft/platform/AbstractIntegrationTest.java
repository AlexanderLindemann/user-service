package com.nft.platform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.nft.platform.initializer.PostgresInitializer;
import com.nft.platform.initializer.WiremockServerInitializer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DBRider
@DBUnit(caseSensitiveTableNames = true,
        leakHunter = true,
        disableSequenceFiltering = true,
        dataTypeFactoryClass = CustomPostgresqlDataTypeFactory.class)
@AutoConfigureMockMvc(addFilters = false)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
        initializers = {
                PostgresInitializer.class,
                WiremockServerInitializer.class
        }
)
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractIntegrationTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @SneakyThrows
    protected DocumentContext parseResponse(String body) {
        return JsonPath.parse(body, Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
    }

    protected <T> T getBody(String body, TypeReference<T> responseBodyType) throws Exception {
        return mapper.readValue(body, responseBodyType);
    }
}
