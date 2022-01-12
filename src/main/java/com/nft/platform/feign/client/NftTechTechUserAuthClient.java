package com.nft.platform.feign.client;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = NftTechTechUserAuthClient.NAME,
        url = "${feign." + NftTechTechUserAuthClient.NAME + ".url}",
        path = "${feign." + NftTechTechUserAuthClient.NAME + ".path}",
        configuration = NftTechTechUserAuthClient.Configuration.class
)
public interface NftTechTechUserAuthClient {
    String NAME = "nft-admin-auth";

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Map<String, Object> getAccessToken(@RequestBody Map<String, ?> request);

    class Configuration {
        @Bean
        Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
            return new SpringFormEncoder(new SpringEncoder(converters));
        }
    }
}
