package com.nft.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import com.nft.platform.initializer.PostgresInitializer;
import com.nft.platform.initializer.WiremockServerInitializer;
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
public class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

}
