package com.nft.platform.dto.poe.request;

import com.nft.platform.dto.poe.AbstractPoeTransactionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Poe Transaction DTO")
public class PoeTransactionRequestDto extends AbstractPoeTransactionDto {

}
