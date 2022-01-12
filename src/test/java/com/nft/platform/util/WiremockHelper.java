package com.nft.platform.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.nft.platform.initializer.WiremockServerInitializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@Component
@RequiredArgsConstructor
public class WiremockHelper {

    protected String mappingPrefix = "";
    protected final ObjectMapper objectMapper;

    @Qualifier(WiremockServerInitializer.WIREMOCK_SERVER_BEAN_NAME)
    @Autowired
    private WireMockServer wireMockServer;

    public void addStaticGetStub(String mapping) {
        addStaticGetStub(mapping, null);
    }

    public void addStaticGetStub(String mapping, Object response) {
        addStaticStub(WireMock::get, mapping, response);
    }

    public void addStaticDeleteStub(String mapping, Object response) {
        addStaticStub(WireMock::delete, mapping, response);
    }

    public void addStaticPostStub(String mapping) {
        addStaticPostStub(mapping, null);
    }

    public void addStaticPostStub(String mapping, Object response) {
        addStaticStub(WireMock::post, mapping, response);
    }

    public void addStaticPutStub(String mapping) {
        addStaticStub(WireMock::put, mapping, null);
    }

    public void addStaticPutStub(String mapping, Object response) {
        addStaticStub(WireMock::put, mapping, response);
    }

    public void addStaticStub(Function<UrlPattern, MappingBuilder> builderFun, String mapping, Object response) {
        var pattern = parseUrlPattern(mapping);
        MappingBuilder stub = builderFun.apply(pattern)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(asResponseString(response))
                )
                ;

        wireMockServer.stubFor(stub);
    }

    private UrlPattern parseUrlPattern(String mapping) {
        UrlPattern pattern;
        if (mapping.startsWith("contains:"))
            pattern = new UrlPattern(new ContainsPattern(applyPrefix(mapping.substring("contains:".length()))), true);
        else if (mapping.startsWith("regex:"))
            pattern = urlPathMatching(applyPrefix(mapping.substring("regex:".length())));
        else if (mapping.startsWith("mask:"))
            pattern = urlPathMatching(getRegexFromMask(applyPrefix(mapping.substring("mask:".length()))));
        else
            pattern = urlEqualTo(applyPrefix(mapping));

        return pattern;
    }

    private String applyPrefix(String str) {
        return mappingPrefix == null || mappingPrefix.isBlank() ? str : mappingPrefix + str;
    }

    @SneakyThrows
    private String asResponseString(Object obj) {
        if (obj == null) {
            return null;
        }

        return objectMapper.writeValueAsString(obj);
    }

    public boolean hasNoPrefix() {
        return mappingPrefix == null || mappingPrefix.isBlank();
    }

    public WiremockHelper withPrefix(String prefix) {
        String newPrefix = hasNoPrefix() ? prefix : mappingPrefix + prefix;
        WiremockHelper result = new WiremockHelper(objectMapper);
        result.mappingPrefix = newPrefix;
        return result;
    }

    public String getMappingPrefix() {
        return mappingPrefix;
    }

    private static String getRegexFromMask(String str) {
        String res = str;
        String[] arr = new String[]{"\\", "#", "|", "(", ")", "[", "]", "{", "}", "^", "$", "+", "."};

        for (String s : arr) {
            res = res.replace(s, "\\" + s);
        }

        res = res.replace("*", ".*");
        res = res.replace("?", ".");
        return "^" + res + "$";
    }

}
