package com.nobody.nobodyplace.utils.redis.impl;

import com.nobody.nobodyplace.utils.redis.ILock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock {

    private final StringRedisTemplate stringRedisTemplate;
    private final String lockName;

    private static final String KEY_PREFIX = "lock:";
    private static final String ID_PREFIX = UUID.randomUUID() + "-";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("release_order_lock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public SimpleRedisLock(StringRedisTemplate stringRedisTemplate, String lockName) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + lockName,
                threadId, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

//    @Override
//    public void unlock() {
//        String threadId = ID_PREFIX + Thread.currentThread().getId();
//        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + lockName);
//        if (threadId.equals(id)) {
//            stringRedisTemplate.delete(KEY_PREFIX + lockName);
//        }
//    }

    @Override
    public void unlock() {
        // 调用 Lua 脚本
        // 一行代码、原子
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                new ArrayList<>(List.of(KEY_PREFIX + lockName)),
                ID_PREFIX + Thread.currentThread().getId());
    }
}
