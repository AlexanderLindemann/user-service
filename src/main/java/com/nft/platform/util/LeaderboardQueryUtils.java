package com.nft.platform.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LeaderboardQueryUtils {

    public static final String REFRESH_QUERY = "REFRESH MATERIALIZED VIEW CONCURRENTLY leaderboard_numbers";

    public static final String FIND_LEADERBOARD_BY_USER_ID = ""
            + "SELECT ln.row_number, ln.points, cast(up.id as varchar), up.username, up.image_url, up.is_invisible_name "
            + "FROM leaderboard_numbers ln JOIN user_profile up ON "
            + "ln.keycloak_user_id = up.keycloak_user_id "
            + "WHERE up.id = :user_id "
            + "AND period_id = (SELECT id FROM period WHERE status = 'ACTIVE')";

    public static final String FIND_LEADERBOARD_BY_KEYCLOAK_USER_ID = ""
            + "SELECT ln.row_number, ln.points, cast(up.id as varchar), up.username, up.image_url, up.is_invisible_name "
            + "FROM leaderboard_numbers ln JOIN user_profile up ON "
            + "ln.keycloak_user_id = up.keycloak_user_id "
            + "WHERE ln.keycloak_user_id = :keycloak_user_id "
            + "AND period_id = (SELECT id FROM period WHERE status = 'ACTIVE')";

    public static final String FIND_LEADERBOARD_ALL_USER = "" +
            "SELECT ln.row_number, cast(ln.keycloak_user_id as varchar), ln.points, cast(up.id as varchar), up.username, up.image_url " +
            "FROM leaderboard_numbers ln JOIN user_profile up " +
            "ON ln.keycloak_user_id = up.keycloak_user_id WHERE period_id = (SELECT id FROM period WHERE status = 'ACTIVE') " +
            "ORDER BY row_number";
}
