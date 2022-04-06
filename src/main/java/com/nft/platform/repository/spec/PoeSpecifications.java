package com.nft.platform.repository.spec;

import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.Poe_;
import com.nft.platform.dto.poe.request.PoeFilterDto;
import com.nft.platform.enums.PoeGroup;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Set;

import static org.springframework.data.jpa.domain.Specification.where;

@UtilityClass
public class PoeSpecifications {

    public static Specification<Poe> from(PoeFilterDto filterDto) {
        return where(groupIn(filterDto.getGroups()));
    }

    public static Specification<Poe> groupIn(Set<PoeGroup> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get(Poe_.group).in(groups);
    }
}
