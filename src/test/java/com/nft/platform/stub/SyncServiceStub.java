package com.nft.platform.stub;

import com.nft.platform.redis.starter.service.SyncService;
import com.nft.platform.redis.starter.util.VoidCallable;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(
        prefix = "redis",
        name = {"enabled"},
        havingValue = "false"
)
@Primary
@Service
public class SyncServiceStub implements SyncService {

    public LockBuilderImpl doSynchronized(RLock lock) {
        return new LockBuilderImpl();
    }

    public RLock getNamedLock(@NonNull String lockName) {
        return null;
    }

    @Override
    public void unlockLock(RLock rLock) {

    }

    public static class LockBuilderImpl implements LockBuilder {

        @Override
        public LockBuilder timeout(long l) {
            return null;
        }

        @Override
        public LockBuilder timeout(TimeUnit timeUnit, long l) {
            return null;
        }

        @SneakyThrows
        public void run(VoidCallable callable) {
            callable.callVoid();
        }

        @SneakyThrows
        public <T> T run(Callable<T> callable) {
            return callable.call();
        }

        @Override
        public LockBuilder unlockAfterTransactionEnd() {
            return null;
        }

        @Override
        public LockBuilder unlockAfterActionEnd() {
            return null;
        }
    }

}
