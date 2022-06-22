package com.nft.platform.service;

import com.nft.platform.domain.CryptoWallet;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.domain.UserProfile;
import com.nft.platform.dto.CelebrityFanDto;
import com.nft.platform.dto.TopFansDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@AllArgsConstructor
public class FanService {
    private static final String AGGREGATION_AVATAR = "";

    private final CryptoWalletService cryptoWalletService;
    private final EntityManagerFactory entityManagerFactory;

    public List<CelebrityFanDto> getTopFansWithSum(UUID celebrityId, int topNumber, int sumNumber) {
        TopFansDto top = getTopFans(celebrityId, 3, 100);
        List<CelebrityFanDto> topFans = top.getFans();
        topFans.add(top.getFansSum());
        return topFans;
    }

    public TopFansDto getTopFans(UUID celebrityId, int topNumber, int sumNumber) {
        log.info("Evaluating top {} fans for celebrity {} with aggregation by {}", topNumber, celebrityId, sumNumber);
        List<CelebrityFanDto> fans = getFans(celebrityId);
        return createTopFansDto(fans, topNumber, sumNumber);
    }

    static TopFansDto createTopFansDto(List<CelebrityFanDto> fans, int topNumber, int sumNumber) {
        var initialSum = CelebrityFanDto.builder()
                .top(sumNumber)
                .avatar(AGGREGATION_AVATAR)
                .token(BigDecimal.ZERO)
                .build();
        CelebrityFanDto sum = fans.stream()
                .limit(sumNumber)
                .reduce(initialSum, (s, item) -> s.toBuilder()
                        .token(s.getToken().add(item.getToken()))
                        .build());
        return TopFansDto.builder()
                .fans(fans.stream()
                        .limit(topNumber)
                        .collect(Collectors.toList()))
                .fansSum(sum)
                .build();
    }

    public List<CelebrityFanDto> getFans(UUID celebrityId) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<ProfileWallet> fanWallets = entityManager
                    .createNamedQuery("ProfileWallet.findAllCelebrityFans")
                    .setParameter("celebrityId", celebrityId)
                    .getResultList();
            List<CelebrityFanDto> fans = createFans(fanWallets);
            return numerate(fans);
        }
        finally {
            entityManager.close();
        }
    }

    List<CelebrityFanDto> createFans(List<ProfileWallet> fanWallets) {
        return fanWallets.stream()
                .map(w -> CelebrityFanDto.builder()
                        .id(w.getUserProfile().getId())
                        .avatar(w.getUserProfile().getImageUrl())
                        .token(getCryptoBalance(w.getUserProfile()).orElse(BigDecimal.ZERO))
                        .usdt(null)
                        .build())
                .sorted(Comparator.comparing(CelebrityFanDto::getToken).reversed())
                .collect(Collectors.toList());
    }

    static List<CelebrityFanDto> numerate(List<CelebrityFanDto> fans) {
        Map<UUID, Integer> userPosition = IntStream.range(0, fans.size()).boxed()
                .collect(Collectors.toMap(i -> fans.get(i).getId(), i -> i));

        return fans.stream()
                .map(fan -> fan.toBuilder()
                        .top(userPosition.get(fan.getId()) + 1)
                        .build()
                ).collect(Collectors.toList());
    }

    Optional<BigDecimal> getCryptoBalance(UserProfile userProfile) {
        return userProfile.getDefaultCryptoWallet()
                .map(CryptoWallet::getExternalCryptoWalletId)
                .map(cryptoWalletService::getCryptoWalletBalance);
    }
}
