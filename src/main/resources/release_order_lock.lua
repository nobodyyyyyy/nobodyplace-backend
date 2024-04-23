-- 锁 id
local key = KEYS[1]

-- 当前线程标识
local current_thread_id = ARGV[1]

-- 获取锁中的线程标识
local thread_id = redis.call('get', key)

if (thread_id == current_thread_id) then
    return redis.call('del', key)  -- 成功 1
end
return 0