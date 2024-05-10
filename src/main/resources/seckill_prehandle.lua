local coupon_id = ARGV[1]
local user_id = ARGV[2]
local order_id = ARGV[3]

local stock_key = "seckill:stock:" .. coupon_id
local order_key = "seckill:order:" .. coupon_id

local stock_res = redis.call('get', stock_key)
if (stock_res == nil) then
    return 1
end
if (tonumber(redis.call('get', stock_key)) <= 0) then
    return 1
end

if (redis.call('sismember', order_key, user_id) == 1) then
    return 2
end

-- 下单，加用户、扣库存
redis.call('incrby', stock_key, -1)
redis.call('sadd', order_key, user_id)

---- 发消息到队列中 XADD stream.orders * key1 val1 key2 val2 (* 代表redis自己生成唯一id)
--redis.call('xadd', 'stream.orders', '*', 'userId', user_id, 'couponId', coupon_id, 'id', order_id)

-- 进一步优化，我们不使用 Stream，而是引入 RocketMQ

return 0
