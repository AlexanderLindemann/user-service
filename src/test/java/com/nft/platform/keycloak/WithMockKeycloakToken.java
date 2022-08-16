package com.nft.platform.keycloak;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creates KeycloakUserProfile from annotation parameters. See {@link WithMockKeycloakTokenSecurityContextFactory}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockKeycloakTokenSecurityContextFactory.class)
public @interface WithMockKeycloakToken {
    String id() default "111142d3-4444-33ef-adfb-d4d03e436744";

    String email() default "";

    String firstName() default "";

    String lastName() default "";

    String preferredUsername() default "";

    String[] roles() default {};
}
