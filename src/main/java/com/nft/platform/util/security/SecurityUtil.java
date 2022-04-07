package com.nft.platform.util.security;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtil {

    public KeycloakUserProfile getCurrentUser() {
        KeycloakUserProfile currentUser = getCurrentUserOrNull();
        if (currentUser == null) {
            throw new InsufficientAuthenticationException("Can't get userId from securityContext");
        }
        return currentUser;
    }

    public KeycloakUserProfile getCurrentUserOrNull() {
        var securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            var authentication = securityContext.getAuthentication();
            if (authentication instanceof OAuth2Authentication) {
                OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
                return (KeycloakUserProfile) oAuth2Authentication.getOAuth2Request()
                        .getExtensions()
                        .get(JwtAccessTokenCustomizer.USER_PROFILE);
            }
        }
        return null;
    }

    public UUID getCurrentUserId() {
        return UUID.fromString(getCurrentUser().getId());
    }

    public String getCurrentUserPreferredName() {
        return getCurrentUser().getPreferredUsername();
    }
}
