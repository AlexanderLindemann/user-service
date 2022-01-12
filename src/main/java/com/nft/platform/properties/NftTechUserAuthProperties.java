package com.nft.platform.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = NftTechUserAuthProperties.PREFIX)
public class NftTechUserAuthProperties {
    public static final String PREFIX = "nft.tech.user.auth";

    private String clientId;
    private String username;
    private String password;
}
