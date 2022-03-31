package com.nft.platform.mapper.poe;

import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.poe.request.PoeRequestDto;
import com.nft.platform.mapper.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PoeFromToRequestMapper extends IMapper<Poe, PoeRequestDto> {
}
