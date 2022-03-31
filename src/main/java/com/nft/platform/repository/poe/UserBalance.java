package com.nft.platform.repository.poe;

import java.util.UUID;

public interface UserBalance {

    UUID getUserId();

    int getActivityBalance();

    int getRowNumber();
}
