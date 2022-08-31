package com.nft.platform.event.producer.impl;

import com.nft.platform.annotation.OnKafkaProducerEnabled;
import com.nft.platform.common.enums.ActivityType;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.event.RewardTransactionEvent;
import com.nft.platform.common.event.TransactionEvent;
import com.nft.platform.dto.poe.response.PoeTransactionResponseDto;
import com.nft.platform.event.producer.IncomeHistoryTransactionEventProducer;
import com.nft.platform.sender.KafkaEventSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Service
@OnKafkaProducerEnabled
@RequiredArgsConstructor
public class IncomeHistoryTransactionEventProducerImpl implements IncomeHistoryTransactionEventProducer {

    private final KafkaEventSender kafkaEventSender;

    @Value("${spring.kafka.producer.income-history.topic}")
    private String topic;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransactionEvent event) {
        log.info("Try to send event={}, topic={}", event, topic);
        kafkaEventSender.send(event, topic);
    }

    public static RewardTransactionEvent formRewardTransactionEvent(
            PoeTransactionResponseDto responseDto,
            Integer coins,
            Integer spins,
            Integer votes,
            Integer nftVotes,
            UUID nft,
            UUID collectible,
            Boolean goldStatus,
            ActivityType activityType
    ) {
        return RewardTransactionEvent.builder()
                .actionId(responseDto.getActionId())
                .activityType(activityType)
                .celebrityId(responseDto.getCelebrityId())
                .userId(responseDto.getUserId())
                .periodId(responseDto.getPeriodId())
                .coinsReward(coins)
                .spinReward(spins)
                .votesReward(votes)
                .nftVotesReward(nftVotes)
                .nftReward(nft)
                .collectibleReward(collectible)
                .gsReward(goldStatus)
                .eventType(EventType.REWARD_TRANSACTION_CREATED)
            .build();
    }


}