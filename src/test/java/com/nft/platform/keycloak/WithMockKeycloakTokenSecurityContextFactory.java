package com.nft.platform.keycloak;

import com.nft.platform.util.security.KeycloakUserProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WithMockKeycloakTokenSecurityContextFactory implements WithSecurityContextFactory<WithMockKeycloakToken> {

    @Override
    public SecurityContext createSecurityContext(WithMockKeycloakToken annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        var authorities = Arrays.stream(annotation.roles())
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
        KeycloakUserProfile userProfile = new KeycloakUserProfile();
        userProfile.setId(annotation.id());
        userProfile.setEmail(annotation.email());
        userProfile.setFirstName(annotation.firstName());
        userProfile.setLastName(annotation.lastName());
        userProfile.setRoles(List.of(annotation.roles()));
        OAuth2Request request = new OAuth2Request(null, null, authorities, true, null, null, null, null, null);
        request.getExtensions().put("user_profile", userProfile);
        Authentication auth = new OAuth2Authentication(request, null);
        context.setAuthentication(auth);
        return context;
    }
}
