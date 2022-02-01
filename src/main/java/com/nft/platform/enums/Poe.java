package com.nft.platform.enums;

public enum Poe {
    VOTE("vote");

    private final String name;

    Poe(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
