package com.nft.platform.util;

import com.nft.platform.NftUserServiceApplication;
import com.nft.platform.util.initializer.PostgresInitializer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
        initializers = {
                PostgresInitializer.class,
        }
)
@ActiveProfiles("test")
@SpringBootTest(classes = {NftUserServiceApplication.class})
public class AbstractIntegrationTest {
}
