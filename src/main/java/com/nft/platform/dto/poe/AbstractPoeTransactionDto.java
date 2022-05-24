package com.nft.platform.dto.poe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AbstractPoeTransactionDto {

    @Schema(description = "Keycloak User Id", required = true)
    @NotNull
    private UUID userId;

    @Schema(description = "Celebrity Id", required = true)
    @NotNull
    private UUID celebrityId;

    private UUID actionId;
}
