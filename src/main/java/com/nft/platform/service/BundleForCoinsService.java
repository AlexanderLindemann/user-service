package com.nft.platform.service;

import com.nft.platform.common.enums.EventType;
import com.nft.platform.common.event.BundleTransactionEvent;
import com.nft.platform.domain.BundleForCoins;
import com.nft.platform.domain.BundleForCoins_;
import com.nft.platform.domain.ProfileWallet;
import com.nft.platform.dto.enums.BundleType;
import com.nft.platform.dto.request.PurchaseForCoinsRequestDto;
import com.nft.platform.dto.response.BundleForCoinsResponseDto;
import com.nft.platform.exception.ItemNotFoundException;
import com.nft.platform.exception.RestException;
import com.nft.platform.mapper.BundleForCoinsMapper;
import com.nft.platform.repository.ProfileWalletRepository;
import com.nft.platform.repository.BundleForCoinsRepository;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BundleForCoinsService {

    @Value("${nft.celebrity.default-uuid}")
    private String defaultCelebrity;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final BundleForCoinsMapper bundleForCoinsMapper;
    private final BundleForCoinsRepository bundleForCoinsRepository;
    private final ProfileWalletRepository profileWalletRepository;
    private final SecurityUtil securityUtil;
    private final ProfileWalletService profileWalletService;

    @Transactional
    public void buyBundleForCoins(PurchaseForCoinsRequestDto requestDto) {
        UUID keycloakUserId = securityUtil.getCurrentUserId();
        UUID celebrityId = UUID.fromString(defaultCelebrity);
        ProfileWallet profileWallet = profileWalletService.getProfileWalletForUpdate(keycloakUserId, celebrityId);
        BundleForCoins bundle = bundleForCoinsRepository.findById(requestDto.getBundleForCoinsId())
                .orElseThrow(() -> new ItemNotFoundException(BundleForCoins.class, requestDto.getBundleForCoinsId()));
        int coins = bundle.getCoins();
        if (profileWallet.getCoinBalance() < coins) {
            throw new RestException("Not enough coins", HttpStatus.CONFLICT);
        }
        if (bundle.getType() == BundleType.VOTE) {
            profileWallet.setVoteBalance(profileWallet.getVoteBalance() + bundle.getBundleSize());
        }
        if (bundle.getType() == BundleType.WHEEL_SPIN) {
            profileWallet.setWheelBalance(profileWallet.getWheelBalance() + bundle.getBundleSize());
        }
        if (bundle.getType() == BundleType.NFT_VOTE) {
            profileWallet.setNftVotesBalance(profileWallet.getNftVotesBalance() + bundle.getBundleSize());
        }
        profileWallet.setCoinBalance(profileWallet.getCoinBalance() - coins);
        profileWalletRepository.save(profileWallet);
        applicationEventPublisher.publishEvent(
            BundleTransactionEvent.builder()
                .celebrityId(celebrityId)
                .userId(keycloakUserId)
                .bundleId(bundle.getId())
                .cost(bundle.getCoins())
                .quantity(bundle.getBundleSize())
                .eventType(EventType.BUNDLE_TRANSACTION_CREATED)
            .build()
        );
    }

    @Transactional(readOnly = true)
    public List<BundleForCoinsResponseDto> getBundles(BundleType type) {
        List<BundleForCoins> bundles = bundleForCoinsRepository.findByType(type, Sort.by(BundleForCoins_.BUNDLE_SIZE));
        BundleForCoins bundleWithMinSize = bundles.stream()
                .min(Comparator.comparing(BundleForCoins::getBundleSize))
                .orElseThrow(() -> new RestException("Bundle with type=" + type + " does not exists.", HttpStatus.BAD_REQUEST));
        double maxPriceForOneLot = countPriceOfOneLot(bundleWithMinSize);
        return bundles.stream()
                .map(bundle -> convertAndCalculateDiscount(bundle, bundleWithMinSize, maxPriceForOneLot))
                .collect(Collectors.toList());
    }

    private BundleForCoinsResponseDto convertAndCalculateDiscount(
            BundleForCoins bundleForCoins,
            BundleForCoins bundleWithMinSize,
            double maxPriceForOneLot
    ) {
        BundleForCoinsResponseDto dto = bundleForCoinsMapper.toDto(bundleForCoins);
        if (bundleForCoins.getId().equals(bundleWithMinSize.getId())) {
            dto.setDiscount(0);
        } else {
            double priceForOneLot = countPriceOfOneLot(bundleForCoins);
            int discount = (int) (100 * (maxPriceForOneLot - priceForOneLot) / maxPriceForOneLot);
            dto.setDiscount(Math.max(discount, 0));
        }
        return dto;
    }

    private double countPriceOfOneLot(BundleForCoins bundleForCoins) {
        return 1.0 * bundleForCoins.getCoins() / bundleForCoins.getBundleSize();
    }

}
