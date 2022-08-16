package com.nft.platform.exception;

import com.nft.platform.common.enums.PoeAction;

import java.util.UUID;

public class InvalidPoeTransactionException extends RuntimeException {
    private static final String ONCE_PER_PERIOD_TEMPLATE = "User = %s already has poe action = %s in period id = %s";

    public InvalidPoeTransactionException(PoeAction action, UUID userid, UUID periodId) {
        super(String.format(ONCE_PER_PERIOD_TEMPLATE, userid, action, periodId));
    }
}
