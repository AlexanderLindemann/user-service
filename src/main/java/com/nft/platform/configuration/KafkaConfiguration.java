package com.nft.platform.configuration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "spring.kafka.consumer.poll", name = "enabled", havingValue = "true")
@EnableKafka
@RequiredArgsConstructor
@Setter
public class KafkaConfiguration {

    @Value("${spring.kafka.consumer.poll.bootstrap-servers:}")
    private String pollBootstrapServer;

    @Bean("pollKafkaConsumerFactory")
    public ConsumerFactory<String, String> pollKafkaConsumerFactory() {
        Map<String, Object> producerFactoryConfig = new HashMap();
        producerFactoryConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, pollBootstrapServer);
        producerFactoryConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        producerFactoryConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        producerFactoryConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service");
        return new DefaultKafkaConsumerFactory<>(producerFactoryConfig);
    }

    @Bean("pollKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> pollKafkaListenerContainerFactory(
            @Qualifier("pollKafkaConsumerFactory") ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
