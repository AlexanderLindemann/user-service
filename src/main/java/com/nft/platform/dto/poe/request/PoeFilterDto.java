package com.nft.platform.dto.poe.request;

import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.common.enums.PoeGroup;
import lombok.Data;

import java.util.Set;

@Data
public class PoeFilterDto {

    private Set<PoeGroup> groups;
    private Set<PoeAction> poeActions;
}
