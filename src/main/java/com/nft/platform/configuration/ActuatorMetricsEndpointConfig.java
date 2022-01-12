package com.nft.platform.configuration;

import com.nft.platform.actuator.endpoint.ActuatorMetricsEndpoint;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(MetricsEndpointAutoConfiguration.class)
public class ActuatorMetricsEndpointConfig {

    /**
     * <p>ActuatorMetricsEndpointConfig.</p>
     *
     * @param meterRegistry a {@link MeterRegistry} object.
     * @return a {@link ActuatorMetricsEndpoint} object.
     */
    @Bean
    public ActuatorMetricsEndpoint advMetricsEndpoint(MeterRegistry meterRegistry) {
        return new ActuatorMetricsEndpoint(meterRegistry);
    }
}
