package com.nft.platform.configuration;

import com.nft.platform.properties.LoggingProperties;
import com.nft.platform.properties.NftTechUserAuthProperties;
import com.nft.platform.properties.PeriodProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
    "com.nft.platform.service",
    "com.nft.platform.repository",
    "com.nft.platform.mapper"
})
@EnableConfigurationProperties(value = {LoggingProperties.class, NftTechUserAuthProperties.class, PeriodProperties.class})
@Import({TestDatabasePersistenceConfiguration.class, MockConfiguration.class, LiquibaseConfiguration.class})
public class UnitTestConfiguration {}