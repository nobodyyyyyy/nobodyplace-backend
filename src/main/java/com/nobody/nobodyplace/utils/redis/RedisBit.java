package com.nobody.nobodyplace.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import java.util.Arrays;

/**
 * 位图操作（节省空间）
 *
 * 存储偏移量offset从左到右，从0开始，每8位一个字符，若偏移量为8，则为第2个字符的最高位
 * 按位为单位来存储数据，可节省空间
 *
 * 使用方式：零存零取，零存整取，整存零取
 * 零存零取：即按位存入，按位取出
 * 零存整取：即按位存入，按字符取出
 * 整存零取：即按字符存入，按位取出
 *
 * 字符为ASCII对应的字节组成，若为不可打印字符，则为对应的16进制数
 */
@Component
public class RedisBit {

    private final StringRedisTemplate redisTemplate;

    public RedisBit(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * setBit 设置某一位的值（零存）
     * @param key redis key
     * @param offset 偏移量：从左到右，从0开始，每8位一个字符，若偏移量为8，则为第2个字符的最高位
     * @param value 位的值  true为1，false为0
     * @return
     */
    public Boolean setBit(String key, long offset, boolean value){
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * getBit 获取某一位的值（零取）
     * @param key redis key
     * @param offset 偏移量：从左到右，从0开始，每8位一个字符，若偏移量为8，则为第2个字符的最高位
     * @return
     */
    public Boolean getBit(String key, long offset){
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 获取指定key的值（整取）
     * @param key
     * @return
     */
    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置指定key的值（整存）
     * @param key
     * @param value
     */
    public void set(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * bitCount 统计值对应位为1的数量
     * @param key redis key
     * @return
     */
    public long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }

    /**
     * bitCount 统计值指定范围（范围为字节范围）对应位为1的数量
     * @param key redis key
     * @param start 开始字节位置（包含）
     * @param end 结束字节位置（包含）
     * @return
     */
    public Long bitCount(String key, long start, long end) {
        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes(), start, end));
    }

    /**
     * bitCountByBitIndex 统计值指定范围（范围为位范围）对应位为1的数量
     * 该方法属于扩展方法，由于redis自身未实现位索引查找，所以实现该方法方便按位查找
     * @param key redis key
     * @param start 开始位位置（包含）
     * @param end 结束位位置（包含）
     * @return
     */
    public Long bitCountByBitIndex(String key, long start, long end) {
        Long c = 0L;
        int k = 8;
        long s = start / k;
        long e = end / k;
        String value = redisTemplate.opsForValue().get(key, s, e);
        int st = (int)(start % k);
        int et = (int)(end % k) + 1;
        for(int i = 0; i < value.length(); i ++){
            int v = value.charAt(i);
            int j = 0;
            if(i == 0){
                j = st;
            }
            if(i == value.length() - 1){
                k = et;
            }
            for(; j < k; j ++){
                c += v >> (7 - j) & 1;
            }
        }
        return c;
    }

    /**
     * bitPos 统计值指定范围（范围为字节范围）内第一个0或1
     * 该方法属于扩展方法，由于redisTemplate未实现该方法，所以以该方法代替，当然，也可以使用Lua脚本进行实现
     * @param key redis key
     * @param value 0（false）或1（true）
     * @param start 开始字节位置（包含）
     * @param end 结束字节位置（包含）
     * @return
     */
    public long bitPos(String key, boolean value, long start, long end) {
        String v = redisTemplate.opsForValue().get(key, start, end);
        for(int i = 0; i < v.length(); i ++){
            int vc = v.charAt(i);
            if((vc & 255) == 0){
                if(value){
                    continue;
                }else{
                    return i * 8;
                }
            }
            for(int j = 0; j < 8; j ++){
                if((vc & (1 << (7 - j))) == 0){
                    if(value){
                        continue;
                    }else{
                        return i * 8 + j;
                    }
                }else{
                    if(value){
                        return i * 8 + j;
                    }else{
                        continue;
                    }
                }
            }
        }
        return -1;
    }

    private static RedisScript<Long> bitPosScript = new DefaultRedisScript<>("return redis.call('bitpos', KEYS[1], ARGV[1], ARGV[2], ARGV[3])", Long.class);

    /**
     * bitPos 统计值指定范围（范围为字节范围）内第一个0或1
     * 使用Lua脚本进行调用redis
     * @param key redis key
     * @param value 0（false）或1（true）
     * @param start 开始字节位置（包含）
     * @param end 结束字节位置（包含）
     * @return
     */
    public Long bitPosByLua(String key, boolean value, long start, long end) {
        return redisTemplate.execute(bitPosScript, Arrays.asList(key), value ? "1" : "0", String.valueOf(start), String.valueOf(end));
    }

    /**
     * bitPos 统计值指定范围（范围为位范围）内第一个0或1
     * 该方法属于扩展方法，由于redis自身未实现位索引查找，所以实现该方法方便按位查找
     * @param key redis key
     * @param value 0（false）或1（true）
     * @param start 开始字节位置（包含）
     * @param end 结束字节位置（包含）
     * @return
     */
    public int bitPosByBitIndex(String key, boolean value, long start, long end) {
        int k = 8;
        long s = start / k;
        long e = end / k;
        String v1 = redisTemplate.opsForValue().get(key, s, e);
        int st = (int)(start % k);
        int et = (int)(end % k) + 1;
        for(int i = 0; i < v1.length(); i ++){
            int v = v1.charAt(i);
            int j = 0;

            if(i == 0){
                j = st;
            }
            if(i == v1.length() - 1){
                k = et;
            }
            for(; j < k; j ++){
                int bit = (v >> (7 - j) & 1);
                if(value ^ bit == 0){
                    return i * 8 + j - st;
                }
            }
        }
        return -1;
    }

}
