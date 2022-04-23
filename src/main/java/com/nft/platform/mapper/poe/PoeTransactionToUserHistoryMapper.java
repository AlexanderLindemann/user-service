package com.nft.platform.mapper.poe;

import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.dto.poe.response.PoeTransactionUserHistoryDto;
import org.springframework.stereotype.Component;

@Component
public class PoeTransactionToUserHistoryMapper {

    public PoeTransactionUserHistoryDto convert(PoeTransaction poeTransaction) {
        return PoeTransactionUserHistoryDto.builder()
                .poeName(poeTransaction.getPoe().getName())
                .points(poeTransaction.getPointsReward())
                .createdAt(poeTransaction.getCreatedAt())
                .build();
    }
}
