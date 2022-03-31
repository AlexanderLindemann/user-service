package com.nft.platform.mapper.poe;

import com.nft.platform.domain.poe.Poe;
import com.nft.platform.dto.poe.response.PoeResponseDto;
import com.nft.platform.mapper.IMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PoeFromToResponseMapper extends IMapper<Poe, PoeResponseDto> {
}
