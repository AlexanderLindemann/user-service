package com.nft.platform.repository.spec;

import com.nft.platform.common.dto.PoeFilterDto;
import com.nft.platform.common.enums.PoeAction;
import com.nft.platform.common.enums.PoeGroup;
import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.Poe_;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Set;

import static org.springframework.data.jpa.domain.Specification.where;

@UtilityClass
public class PoeSpecifications {

    public static Specification<Poe> from(PoeFilterDto filterDto) {
        return where(groupIn(filterDto.getGroups()))
                .and(codeIn(filterDto.getPoeActions()));
    }

    public static Specification<Poe> groupIn(Set<PoeGroup> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get(Poe_.group).in(groups);
    }

    public static Specification<Poe> codeIn(Set<PoeAction> actions) {
        if (CollectionUtils.isEmpty(actions)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get(Poe_.code).in(actions);
    }

    public static Specification<Poe> codeEqual(PoeAction poeAction) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Poe_.code), poeAction);
    }
}
