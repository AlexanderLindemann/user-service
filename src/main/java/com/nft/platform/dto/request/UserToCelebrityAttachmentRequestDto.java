package com.nft.platform.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bundle for Coins Request DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserToCelebrityAttachmentRequestDto {
    @NotNull
    private UUID celebrityId;
}
