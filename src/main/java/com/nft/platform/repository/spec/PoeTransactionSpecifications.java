package com.nft.platform.repository.spec;

import com.nft.platform.domain.poe.PoeTransaction;
import com.nft.platform.domain.poe.PoeTransaction_;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaQuery;
import java.util.UUID;

@UtilityClass
public class PoeTransactionSpecifications {

    public Specification<PoeTransaction> forPoeUserHistory(UUID celebrityId) {
        Specification<PoeTransaction> spec = (root, query, criteriaBuilder) -> {
            if (!currentQueryIsCountRecords(query)) {
                root.fetch(PoeTransaction_.poe);
            }
            return null;
        };
        return spec.and(celebrityEqual(celebrityId))
                .and(pointsGreaterThan(0));
    }

    public Specification<PoeTransaction> pointsGreaterThan(int value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get(PoeTransaction_.pointsReward)),
                criteriaBuilder.greaterThan(root.get(PoeTransaction_.pointsReward), value)
        );
    }

    public Specification<PoeTransaction> celebrityEqual(UUID celebrityId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PoeTransaction_.celebrityId), celebrityId);
    }

    private boolean currentQueryIsCountRecords(CriteriaQuery<?> criteriaQuery) {
        return criteriaQuery.getResultType() == Long.class || criteriaQuery.getResultType() == long.class;
    }
}
