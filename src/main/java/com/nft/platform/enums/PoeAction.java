package com.nft.platform.enums;

import lombok.Getter;

@Getter
public enum PoeAction {

    VOTE("vote"),
    QUIZ("quiz"),
    CHALLENGE("challenge"),
    REGISTRATION("registration"),
    LIKE("like"),
    PROFILE_COMPLETE("profile_complete"),
    PERIOD_ENTRY("period_entry"),
    WATCH_VIDEO("watch_video");

    private final String actionCode;

    PoeAction(String actionCode) {
        this.actionCode = actionCode;
    }

}
