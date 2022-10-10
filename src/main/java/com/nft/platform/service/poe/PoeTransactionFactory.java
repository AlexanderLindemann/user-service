package com.nft.platform.service.poe;

import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.common.event.AuthUserAuthorizedEvent;
import com.nft.platform.common.util.EnumUtil;
import com.nft.platform.domain.Period;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.dto.enums.PeriodStatus;
import com.nft.platform.dto.poe.request.PoeTransactionRequestDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.mapper.poe.PoeTransactionMapper;
import com.nft.platform.repository.PeriodRepository;
import com.nft.platform.repository.poe.PoeRepository;
import com.nft.platform.repository.poe.PoeTransactionRepository;
import com.nft.platform.service.UserProfileService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoeTransactionFactory {
    private final PoeTransactionMapper poeTransactionMapper;
    private final PoeRepository poeRepository;
    private final PeriodRepository periodRepository;
    private final PoeTransactionRepository poeTransactionRepository;
    private final UserProfileService userProfileService;

    public List<PoeTransaction> create(AuthUserAuthorizedEvent event) {
        var eventType = event.getEventType();
        var user = userProfileService.findByLogin(event.getLogin()).orElseThrow(() -> new NoSuchElementException(String.format("Cannot find user %s to award for entry", event.getLogin())));
        if (isPoeInvalidForPeriod(getCurrentPeriodId(), user.getId(), getAction(eventType))) {
            log.warn("User {} is already awarded for authorization in period {}, award will be ignored.", event.getLogin(), getCurrentPeriodId());
            return Collections.emptyList();
        }
        return user.getProfileWallets().stream()
                .map(wallet -> createByAuthEvent(event, user, wallet.getCelebrity().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Optional<PoeTransaction> create(PoeTransactionRequestDto request) {
        var eventType = request.getEventType();
        var poe = findPoeByEventType(eventType);
        var currentPeriod = getCurrentPeriodId();
        if (isPoeInvalidForPeriod(currentPeriod, request.getUserId(), getAction(eventType))) {
            log.error("Cannot create PoeTransaction because of poe was already awarded in current period");
            return Optional.empty();
        }
        PoeTransaction poeTransaction = poeTransactionMapper.toEntity(request, new PoeTransaction());
        poeTransaction.setPeriodId(currentPeriod);
        poeTransaction.setPoe(poe);
        return Optional.of(poeTransaction);
    }

    private Optional<PoeTransaction> createByAuthEvent(AuthUserAuthorizedEvent event, UserProfile user, UUID celebrityId) {
        return create(PoeTransactionRequestDto.builder()
                .userId(user.getKeycloakUserId())
                .amount(1)
                .celebrityId(celebrityId)
                .eventType(event.getEventType())
                .build());
    }

    private PoeAction getAction(EventType type) {
        var action = EnumUtil.EVENT_TO_POE_MAP.get(type);
        if (action == null) {
            throw new ItemNotFoundException(Poe.class, "can not find poe for event=" + type);
        }
        return action;
    }

    private UUID getCurrentPeriodId() {
        return periodRepository.findByStatus(PeriodStatus.ACTIVE)
                .orElseThrow(() -> new ItemNotFoundException(Period.class, "can not find active period"))
                .getId();
    }

    private Poe findPoeByEventType(EventType eventType) {
        var action = getAction(eventType);
        return poeRepository.findByCode(action).orElseThrow(() -> new ItemNotFoundException(Poe.class, "code=" + action.getActionCode()));
    }

    /**
     * Checks if user already has a once-per-period poe transaction in specific period.
     *
     * @param periodId - period UUID
     * @param userId - user UUID
     * @param poeAction - poe action
     * @return - whether if poe action is invalid for specific period
     */
    private boolean isPoeInvalidForPeriod(@NonNull UUID periodId, @NonNull UUID userId, @NonNull PoeAction poeAction) {
        if (EnumUtil.ONCE_FOR_ACTIVE_PERIOD_POE.contains(poeAction)) {
            return poeTransactionRepository.existsByPeriodIdAndUserIdAndPoe_Code(periodId, userId, poeAction);
        }
        return false;
    }
}
