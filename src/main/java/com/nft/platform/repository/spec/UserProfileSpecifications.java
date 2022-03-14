package com.nft.platform.repository.spec;

import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.request.UserProfileFilterDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

public class UserProfileSpecifications {

    public static Specification<UserProfile> fromUserProfileFilter(UserProfileFilterDto filterDto) {
        return where(keycloakUserIdIn(filterDto.getKeycloakUserIds()));
    }

    public static Specification<UserProfile> keycloakUserIdIn(Set<UUID> keycloakUserIds) {
        if (CollectionUtils.isEmpty(keycloakUserIds)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get("keycloakUserId").in(keycloakUserIds);
    }
}

