package com.nft.platform.event.consumer;

public interface PollKafkaEventListener {

    void receive(String event);
}
