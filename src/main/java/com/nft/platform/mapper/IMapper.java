package com.nft.platform.mapper;

import org.mapstruct.MappingTarget;

public interface IMapper<FROM, TO> {

    TO convert(FROM source);

    TO convert(FROM source, @MappingTarget TO target);

    FROM reverse(TO source);

    FROM reverse(TO source, @MappingTarget FROM target);
}
