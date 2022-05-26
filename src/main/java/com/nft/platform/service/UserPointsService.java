package com.nft.platform.service;

import com.nft.platform.domain.UserPoints;
import com.nft.platform.repository.UserPointsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPointsService {

    private final UserPointsRepository userPointsRepository;

    @Transactional
    public void createOrUpdateUserPoints(UUID periodId, UUID keycloakUserId, Integer points) {
        if (userPointsRepository.existsByPeriodIdAndKeycloakUserId(periodId, keycloakUserId)) {
            userPointsRepository.updateUserPoints(periodId, keycloakUserId, points);
        } else {
            UserPoints userPoints = UserPoints.builder()
                    .keycloakUserId(keycloakUserId)
                    .periodId(periodId)
                    .points(points)
                    .build();
            userPointsRepository.save(userPoints);
        }
    }
}
