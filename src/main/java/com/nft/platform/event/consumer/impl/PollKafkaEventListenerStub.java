package com.nft.platform.event.consumer.impl;

import com.nft.platform.event.consumer.KafkaEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "spring.kafka.consumer.poll", name = "enabled", havingValue = "false", matchIfMissing = true)
@Service
@Slf4j
public class PollKafkaEventListenerStub implements KafkaEventListener {

    @Override
    public void receive(String event) {

    }
}
