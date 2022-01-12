package com.nft.platform.feign.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nft.platform.feign.client.NftTechTechUserAuthClient;
import com.nft.platform.properties.NftTechUserAuthProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class NftTechUserJWTAuthRequestInterceptor implements RequestInterceptor {

    @Autowired
    private NftTechTechUserAuthClient nftAdminAuthClient;

    @Autowired
    private NftTechUserAuthProperties adminAuthProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private final AtomicReference<String> accessToken = new AtomicReference<>();
    private final AtomicLong accessTokenExpire = new AtomicLong();

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer " + getAccessToken());
    }

    private String getAccessToken() {
        var token = accessToken.get();
        if (isTokenExpired()) {
            synchronized (accessToken) {
                token = accessToken.get();
                if (isTokenExpired()) {
                    token = updateToken();
                }
            }
        }

        return token;
    }

    private boolean isTokenExpired() {
        var tokenString = accessToken.get();

        if (tokenString == null)
            return true;
        return System.currentTimeMillis() / 1000 + 10 > accessTokenExpire.get();
    }

    @SneakyThrows
    private String updateToken() {
        var tokenAsBase64String = (String) nftAdminAuthClient.getAccessToken(Map.of(
                "grant_type", "password",
                "client_id", adminAuthProperties.getClientId(),
                "username", adminAuthProperties.getUsername(),
                "password", adminAuthProperties.getPassword())
        ).get("access_token");

        var tokenAsJsonString = new String(Base64.getDecoder().decode(tokenAsBase64String.split("\\.")[1]));

        var token = objectMapper.readValue(tokenAsJsonString, Map.class);
        accessTokenExpire.set(Long.parseLong(String.valueOf(token.get("exp"))));
        accessToken.set(tokenAsBase64String);

        return tokenAsBase64String;
    }
}
