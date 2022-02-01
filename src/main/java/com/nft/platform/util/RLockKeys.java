package com.nft.platform.util;

import java.util.UUID;

public class RLockKeys {
    public static final String VOTE_UPDATE_KEY_PREFIX = "update-vote-balance-";

    public static String createVoteUpdateKey(UUID keycloakUserId, UUID celebrityId) {
        return String.format("%s-%s-%s", VOTE_UPDATE_KEY_PREFIX, keycloakUserId.toString(), celebrityId);
    }
}
