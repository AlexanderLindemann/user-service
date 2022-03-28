package com.nft.platform.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

class CelebrityControllerTest extends AbstractControllerTest {
    private static final String API_PATH = "/api/v1/celebrity";

    @Test
    @DisplayName("Проверет что get возвращает 200 без токена авторизации")
    void test_api_get_all_without_auth() {
        given()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Проверет что post  возвращает 401 без токена авторизации")
    void test_api_post_without_auth() {
        given()
                .post()
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Проверет что put возвращает 401 без токена авторизации")
    void test_api_put_without_auth() {
        given()
                .put()
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    String getBasePath() {
        return API_PATH;
    }
}