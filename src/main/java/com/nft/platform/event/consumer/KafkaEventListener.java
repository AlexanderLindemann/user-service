package com.nft.platform.event.consumer;

public interface KafkaEventListener {

    void receive(String event);
}
