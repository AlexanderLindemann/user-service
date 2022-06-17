package com.nft.platform.dto.enums;

import com.nft.platform.exception.RestException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum LeaderboardGroup {

    TOP_10(1),
    TOP_20(2),
    TOP_30(3),
    TOP_40(4),
    TOP_50(5),
    NOT_TOP_50(6);

    private final int number;

    LeaderboardGroup(int number) {
        this.number = number;
    }

    public static LeaderboardGroup findByNumber(int number) {
        return Arrays.stream(LeaderboardGroup.values()).filter(r -> r.getNumber() == number)
                .findFirst()
                .orElseThrow(() -> new RestException("LeaderboardGroup not found by number=" + number, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public static LeaderboardGroup fromPercent(int percent) {
        if (percent <= 10) {
            return TOP_10;
        }
        if (percent <= 20) {
            return TOP_20;
        }
        if (percent <= 30) {
            return TOP_30;
        }
        if (percent <= 40) {
            return TOP_40;
        }
        if (percent <= 50) {
            return TOP_50;
        }
        return NOT_TOP_50;
    }

    public static LeaderboardGroup findPrevious(LeaderboardGroup leaderboardGroup) {
        if (leaderboardGroup == NOT_TOP_50) {
            return TOP_50;
        }
        if (leaderboardGroup == TOP_50) {
            return TOP_40;
        }
        if (leaderboardGroup == TOP_40) {
            return TOP_30;
        }
        if (leaderboardGroup == TOP_30) {
            return TOP_20;
        }
        if (leaderboardGroup == TOP_20) {
            return TOP_10;
        }
        return null;
    }
}
