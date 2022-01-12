package com.nft.platform.configuration;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nft.platform.properties.LoggingProperties;
import com.nft.platform.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class LoggingConfig {

    public LoggingConfig(
            @Value("${spring.application.name}") String appName,
            @Value("${server.port}") String serverPort,
            LoggingProperties loggingProperties,
            ObjectProvider<BuildProperties> buildProperties,
            ObjectMapper mapper
    ) throws JsonProcessingException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Map<String, String> map = new HashMap<>();
        map.put("app_name", appName);
        map.put("app_port", serverPort);
        buildProperties.ifAvailable(it -> map.put("version", it.getVersion()));
        String customFields = mapper.writeValueAsString(map);

        if (loggingProperties.getLogstash().isEnabled()) {
            LoggingUtils.addLogstashConsoleAppender(context, customFields, loggingProperties.getLogstash().getAddress());
            LoggingUtils.addContextListener(context, customFields, loggingProperties);
        }
    }
}
