package com.nft.platform.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = PeriodProperties.PREFIX)
public class PeriodProperties {
    public static final String PREFIX = "nft.period";

    private int durationSeconds;
    private String cronExpression;
}
