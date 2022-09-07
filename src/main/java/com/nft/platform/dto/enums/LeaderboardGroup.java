package com.nft.platform.dto.enums;

import com.nft.platform.exception.RestException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum LeaderboardGroup {

    TOP_10(0),
    TOP_20(1),
    TOP_30(2),
    TOP_40(3),
    TOP_50(4),
    NOT_TOP_50(5);

    private final int number;

    LeaderboardGroup(int number) {
        this.number = number;
    }

    public static LeaderboardGroup findByNumber(int number) {
        return Arrays.stream(LeaderboardGroup.values())
                .filter(r -> r.getNumber() == number)
                .findFirst()
                .orElseThrow(() -> new RestException("LeaderboardGroup not found by number=" + number, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
