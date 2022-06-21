package com.nft.platform.repository.poe;

import com.nft.platform.domain.poe.Poe;
import com.nft.platform.domain.poe.PoeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PoeTransactionRepository extends JpaRepository<PoeTransaction, UUID>, JpaSpecificationExecutor<PoeTransaction> {

    @Query(value = ""
            + "SELECT SUM (p.pointsReward) AS activityBalance "
            + "FROM PoeTransaction p "
            + "WHERE p.userId = :userId "
            + "AND p.periodId = :periodId "
            + "AND p.celebrityId = :celebrityId "
            + "GROUP BY (p.userId, p.periodId, p.celebrityId)"
    )
    Integer calculateUserActivityBalance(UUID userId, UUID celebrityId, UUID periodId);

    @Query(value = ""
            + "select sum(p.pointsReward) as activityBalance "
            + "from PoeTransaction p "
            + "where p.userId = :userId "
            + "and p.periodId = :periodId "
            + "group by (p.userId, p.periodId)"
    )
    Long calculateUserAllActivityBalance(UUID userId, UUID periodId);

    @Query(value = ""
            + "select t.rowNumber, cast(t.userId as varchar), t.activityBalance from "
            + "("
            + "select "
            + "row_number() over(order by sum(points_reward) desc, max(created_at)) as rowNumber, "
            + "user_id as userId, "
            + "sum(points_reward) as activityBalance "
            + "from poe_transaction p "
            + "where period_id = :periodId "
            + "group by(user_id, period_id) "
            + ") t "
            + "where (t.rowNumber >= :from and t.rowNumber <= :to) "
            + "or t.userId = :userId",
            nativeQuery = true
    )
    List<UserBalance> calculateTopUsersActivityBalance(UUID periodId, UUID userId, Integer from, Integer to);

    @Query(value = ""
            + "select t.rowNumber, cast(t.userId as varchar), t.activityBalance from "
            + "("
            + "select "
            + "row_number() over(order by sum(points_reward) desc, max(created_at)) as rowNumber, "
            + "user_id as userId, "
            + "sum(points_reward) as activityBalance "
            + "from poe_transaction p "
            + "where period_id = :periodId "
            + "group by(user_id, period_id) "
            + ") t ",
            nativeQuery = true
    )
    List<UserBalance> calculateUsersActivityBalance(UUID periodId);

    @Query(value = ""
            + "select count (distinct user_id) "
            + "from poe_transaction p "
            + "where period_id = :periodId",
            nativeQuery = true
    )
    long countDistinctUserIdByPeriodId(UUID periodId);

    List<PoeTransaction> findByActionIdInAndPoeInAndUserId(List<UUID> actionId, List<Poe> poe, UUID userId);
}
