package com.nft.platform.repository.spec;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.domain.Celebrity_;
import com.nft.platform.dto.poe.request.CelebrityFilterRequestDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@UtilityClass
public class CelebritySpecifications {

    public Specification<Celebrity> celebrityFilterSpecification(CelebrityFilterRequestDto filterDto) {
        Specification<Celebrity> spec = Specification.where(null);
        if (Objects.nonNull(filterDto.getName())) {
            spec = spec.and(name(filterDto.getName()));
        }
        if (Objects.nonNull(filterDto.getLastName())) {
            spec = spec.and(lastName(filterDto.getLastName()));
        }
        if (Objects.nonNull(filterDto.getNickName())) {
            spec = spec.and(nickName(filterDto.getNickName()));
        }
        return spec;
    }

    public Specification<Celebrity> name(String searchText) {
        String searchTextLower = searchText.toLowerCase();
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get(Celebrity_.NAME)), "%" + searchTextLower + "%");
    }

    public Specification<Celebrity> lastName(String searchText) {
        String searchTextLower = searchText.toLowerCase();
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get(Celebrity_.LAST_NAME)), "%" + searchTextLower + "%");
    }

    public Specification<Celebrity> nickName(String searchText) {
        String searchTextLower = searchText.toLowerCase();
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get(Celebrity_.NICK_NAME)), "%" + searchTextLower + "%");
    }

    public static Specification<Celebrity> celebrityActiveTrueSpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Celebrity_.ACTIVE), true);
    }
}
