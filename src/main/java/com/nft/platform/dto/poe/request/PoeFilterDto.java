package com.nft.platform.dto.poe.request;

import com.nft.platform.enums.PoeAction;
import com.nft.platform.enums.PoeGroup;
import lombok.Data;

import java.util.Set;

@Data
public class PoeFilterDto {

    private Set<PoeGroup> groups;
    private Set<PoeAction> poeActions;
}
