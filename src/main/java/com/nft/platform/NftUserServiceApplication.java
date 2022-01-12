package com.nft.platform;

import com.nft.platform.properties.LoggingProperties;
import com.nft.platform.properties.NftTechUserAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableOAuth2Client
@EnableFeignClients
@EnableConfigurationProperties({
		LoggingProperties.class,
		NftTechUserAuthProperties.class
})
@SpringBootApplication
public class NftUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NftUserServiceApplication.class, args);
	}

}
