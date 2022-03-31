package com.nft.platform.dto.poe.request;

import com.nft.platform.common.enums.EventType;
import com.nft.platform.dto.poe.AbstractPoeTransactionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Poe Transaction DTO")
public class PoeTransactionRequestDto extends AbstractPoeTransactionDto {

    @Schema(description = "Event Type", required = true)
    @NotNull
    private EventType eventType;

    @Builder.Default
    private Integer amount = 1;
}
