package com.nft.platform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.nft.platform.common.util.JsonUtil;
import com.nft.platform.initializer.PostgresInitializer;
import com.nft.platform.initializer.WiremockServerInitializer;
import com.nft.platform.service.CelebrityService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

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
    @Autowired
    protected WireMockServer wireMockServer;
    @SpyBean
    protected CelebrityService celebrityService;

    @SneakyThrows
    protected DocumentContext parseResponse(String body) {
        return JsonPath.parse(body, Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
    }

    protected <T> T getBody(String body, TypeReference<T> responseBodyType) throws Exception {
        return mapper.readValue(body, responseBodyType);
    }

    protected static class NftServiceMocks {

        public static void setTopCelebrityByNftCountMap(WireMockServer wireMockServer, Map<UUID, Integer> topCelebrityByNftCountMap) {
            wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/v1/nfts/top-celebrity-ids-by-nft-count"))
                    .withQueryParam("limit", WireMock.matching(".*"))
                    .willReturn(WireMock.aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBody(JsonUtil.toJsonString(topCelebrityByNftCountMap))));
        }

    }

}
