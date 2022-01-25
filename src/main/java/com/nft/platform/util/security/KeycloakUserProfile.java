package com.nft.platform.util.security;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class KeycloakUserProfile implements Serializable {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String preferredUsername;
    private List<String> roles;
}
