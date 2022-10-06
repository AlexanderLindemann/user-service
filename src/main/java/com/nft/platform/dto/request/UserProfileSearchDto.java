package com.nft.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Parameters for a search of user profile")
@Builder
public class UserProfileSearchDto {
    private String email;
    private String phone;
    private String name;
}
