package com.nobody.nobodyplace.utils.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {

    private static final long BEGIN_TIMESTAMP = 1709222400L;
    private static final int COUNT_BITS = 32;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public long nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSec = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSec - BEGIN_TIMESTAMP;

        // 每一天重新生成新的 key
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue().increment("irc:" + keyPrefix + ":" + date);
        return (timestamp << COUNT_BITS) | count;
    }
}
