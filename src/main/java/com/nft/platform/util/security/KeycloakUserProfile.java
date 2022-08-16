package com.nft.platform.util.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserProfile implements Serializable {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String preferredUsername;
    private List<String> roles;
    @Deprecated(forRemoval = true)
    // current celebrity will be provided by request parameters
    private String currentCelebrityId;
}
