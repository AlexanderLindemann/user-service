package com.nft.platform.configuration;

import com.nft.platform.initializer.PostgresInitializer;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import java.util.HashMap;

@Configuration
@EnableJpaRepositories(basePackages = {"com.nft.platform.repository"},
    entityManagerFactoryRef = "testEntityManager",
    transactionManagerRef = "testTransactionManager")
@SuppressWarnings("DuplicatedCode")
@EnableTransactionManagement
public class TestDatabasePersistenceConfiguration {

    @Autowired
    private Environment env;

    @Bean
    @Primary
    public DataSourceProperties testDataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl(PostgresInitializer.PG_CONTAINER.getJdbcUrl());
        properties.setUsername("users");
        properties.setPassword("users");
        return properties;
    }

    @Bean
    @Primary
    public DataSource testDataSource() {
        return testDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean testEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(testDataSource());
        em.setPackagesToScan("com.nft.platform.domain");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.hibernate.dialect"));
        properties.put("hibernate.generate_statistics", env.getProperty("spring.jpa.properties.hibernate.generate_statistics"));
        properties.put("hibernate.jdbc.batch_size", env.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size"));
        properties.put("hibernate.physical_naming_strategy", env.getProperty("spring.jpa.properties.hibernate.physical_naming_strategy"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager testTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(testEntityManager().getObject());
        return transactionManager;
    }

}