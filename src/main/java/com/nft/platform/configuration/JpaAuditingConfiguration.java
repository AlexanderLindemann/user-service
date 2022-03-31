package com.nft.platform.configuration;

import com.nft.platform.util.security.KeycloakUserProfile;
import com.nft.platform.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
@RequiredArgsConstructor
public class JpaAuditingConfiguration {

    private final SecurityUtil securityUtil;

    @Bean
    public AuditorAware<String> securityAuditor() {
        return () -> {
            KeycloakUserProfile keycloakUserProfile = securityUtil.getCurrentUserOrNull();
            return Optional.of(keycloakUserProfile == null ? "system" : keycloakUserProfile.getId());
        };
    }

}
