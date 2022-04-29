package com.nft.platform.service;

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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BundleForCoinsService {

    @Value("${nft.celebrity.default-uuid}")
    private String defaultCelebrity;

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
        profileWallet.setCoinBalance(profileWallet.getCoinBalance() - coins);
        profileWalletRepository.save(profileWallet);
    }

    @Transactional(readOnly = true)
    public List<BundleForCoinsResponseDto> getBundles(BundleType type) {
        return bundleForCoinsRepository.findByType(type, Sort.by(BundleForCoins_.BUNDLE_SIZE)).stream()
                .map(bundleForCoinsMapper::toDto)
                .collect(Collectors.toList());
    }

}
