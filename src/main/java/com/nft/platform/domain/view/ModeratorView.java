package com.nft.platform.domain.view;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public interface ModeratorView {

    UUID getKeycloakUserId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

}
