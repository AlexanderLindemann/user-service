package com.nft.platform;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;

import com.nft.platform.feign.client.FileServiceClient;
import com.nft.platform.feign.client.NftServiceApiClient;
import com.nft.platform.feign.client.SolanaAdapterClient;
import com.nft.platform.initializer.PostgresInitializer;
import com.nft.platform.configuration.UnitTestConfiguration;
import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.util.security.SecurityUtil;

import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DBRider
@DBUnit(caseSensitiveTableNames = true,
        leakHunter = true,
        disableSequenceFiltering = true,
        dataTypeFactoryClass = CustomPostgresqlDataTypeFactory.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = { PostgresInitializer.class },classes = { UnitTestConfiguration.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractUnitTest {

    @MockBean
    protected SecurityUtil securityUtil;

    @MockBean
    protected SyncService service;

    @MockBean
    protected SolanaAdapterClient solanaAdapterClient;

    @MockBean
    protected FileServiceClient fileServiceClient;

    @MockBean
    protected NftServiceApiClient nftServiceApiClient;

}