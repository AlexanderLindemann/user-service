package com.nft.platform.dto.poe.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Celebrity Filter Request Dto")
public class CelebrityFilterRequestDto {

    @Schema(description = "Name celebrity filter")
    @Size(max = 1024)
    private String name;

    @Schema(description = "Lastname celebrity filter")
    @Size(max = 255)
    private String lastName;

    @Schema(description = "Nickname celebrity filter")
    @Size(max = 1024)
    private String nickName;
}
