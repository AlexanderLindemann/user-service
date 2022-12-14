package com.nft.platform.service;

import com.nft.platform.common.dto.RewardDto;
import com.nft.platform.common.dto.VariablesDto;
import com.nft.platform.common.enums.ActivityForRewardType;
import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.enums.RewardType;
import com.nft.platform.common.event.NotificationRewardEvent;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.domain.poe.PoeTransaction_;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.event.producer.NotificationServiceRewardTransactionEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:notification.properties")
public class NotificationSenderService {

    private final Environment environment;
    private final NotificationServiceRewardTransactionEventProducer producer;

    public void sendRewardToNotificationService(EventType eventType, PoeTransaction transaction) {
        if ((ActivityForRewardType.contains(eventType.name()))) {
            VariablesDto variables = VariablesDto.builder()
                    .celebrityId(transaction.getCelebrityId())
                    .actionId(transaction.getActionId())
                    .build();

            List<RewardDto> reward = new ArrayList<>();
            if (transaction.getPointsReward() != 0) {
                reward.add(RewardDto.builder()
                        .rewardType(RewardType.POINTS)
                        .quantity(transaction.getPointsReward())
                        .build());
            }

            if (transaction.getCoinsReward() != 0) {
                reward.add(RewardDto.builder()
                        .rewardType(RewardType.COINS)
                        .quantity(transaction.getCoinsReward())
                        .build());
            }

            producer.handle(NotificationRewardEvent.builder()
                    .type(ActivityForRewardType.valueOf(eventType.name()))
                    .text(environment.getProperty(eventType.name()))
                    .userId(transaction.getUserId())
                    .variables(variables)
                    .reward(reward)
                    .build());
        }
    }

}
