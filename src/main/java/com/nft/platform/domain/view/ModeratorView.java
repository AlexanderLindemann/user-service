package com.nft.platform.domain.view;

import java.util.UUID;

public interface ModeratorView {

    UUID getKeycloakUserId();

    String getFirstName();

    String getLastName();
}
