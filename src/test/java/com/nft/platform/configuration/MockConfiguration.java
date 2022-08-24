package com.nft.platform.configuration;

import com.nft.platform.event.producer.impl.IncomeHistoryTransactionEventProducerImpl;
import com.nft.platform.event.producer.impl.PeriodCreatedEventProducerImpl;
import com.nft.platform.event.producer.impl.TokenDistributionTransactionChangedImpl;

import org.mockito.Mockito;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockConfiguration {

    @Bean
    @Primary
    public IncomeHistoryTransactionEventProducerImpl getIncomeHistoryTransactionEventProducer() {
        return Mockito.mock(IncomeHistoryTransactionEventProducerImpl.class);
    }

    @Bean
    @Primary
    public PeriodCreatedEventProducerImpl getPeriodCreatedEventProducer() {
        return Mockito.mock(PeriodCreatedEventProducerImpl.class);
    }

    @Bean
    @Primary
    public TokenDistributionTransactionChangedImpl getTokenDistributionTransactionChangedImpl() {
        return Mockito.mock(TokenDistributionTransactionChangedImpl.class);
    }

}