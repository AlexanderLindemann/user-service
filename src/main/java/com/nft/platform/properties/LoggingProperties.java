package com.nft.platform.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = LoggingProperties.PREFIX)
public class LoggingProperties {
    public static final String PREFIX = "nft.logging";
    private boolean useJsonFormat = false;
    private LogstashProperties logstash = new LogstashProperties();

    @Getter
    @Setter
    public static class LogstashProperties {
        private boolean enabled = false;
        private String address;
    }
}
