package fun.werfamily.starter.cache.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;


    private final RedisSerializer<String> stringSerializer = new StringRedisSerializer();

    public RedisServiceImpl() {
    }

    private final byte[] rawKey(String key) {
        return stringSerializer.serialize(key);
    }

    private final byte[][] rawKeys(Collection<String> keys) {
        final byte[][] rawKeys = new byte[keys.size()][];

        int i = 0;
        for (String key : keys) {
            rawKeys[i++] = rawKey(key);
        }
        return rawKeys;
    }

    private final String stringOf(byte[] value) {
        if (value == null) {
            return "";
        }
        return stringSerializer.deserialize(value);
    }

    /**
     * @param key
     */
    @Override
    public void del(final String key) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.del(rawKey(key));
            }
        });
    }

    /**
     *
     */
    @Override
    public void del(final Collection<String> keys) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.del(rawKeys(keys));
            }
        });
    }

    @Override
    public boolean expire(final String key, final long timeOut) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.expire(rawKey(key), timeOut);
            }
        });
    }

    @Override
    public long ttl(final String key) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.ttl(rawKey(key));
            }
        });
    }

    /**
     * @param key
     * @param value
     */
    private void set(final byte[] key, final byte[] value, final long timeOut) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                if (timeOut > 0) {
                    connection.setEx(key, timeOut, value);
                } else {
                    connection.set(key, value);
                }
                return timeOut;
            }
        });
    }

    /**
     * @param key
     * @param value
     */
    @Override
    public void set(String key, String value, long timeOut) {
        this.set(rawKey(key), rawKey(value), timeOut);
    }

    /**
     * @param key
     * @param value
     */
    @Override
    public void set(String key, String value) {
        this.set(key, value, 0L);
    }

    @Override
    public void set(String key, byte[] value) {
        this.set(rawKey(key), value, 0);

    }

    @Override
    public void set(String key, byte[] value, long timeOut) {
        this.set(rawKey(key), value, timeOut);
    }

    @Override
    public String getAndSet(String key, String value) {
        return getAndSet(key, value, 0);
    }

    @Override
    public String getAndSet(final String key, final String value, final long timeOut) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                byte[] old = connection.getSet(rawKey(key), rawKey(value));
                if (timeOut > 0) {
                    connection.expire(rawKey(key), timeOut);
                }
                return stringOf(old);
            }
        });
    }

    @Override
    public boolean setNX(final String key, final String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.setNX(rawKey(key), rawKey(value));
            }
        });
    }

    @Override
    public boolean setNX(final String key, final String value, long timeOut) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                byte[] lockKey = rawKey(key);
                boolean ret = connection.setNX(lockKey, rawKey(value));
                if (ret && timeOut > 0) {
                    connection.expire(lockKey, timeOut);
                }
                return ret;
            }
        });
    }

    @Override
    public void mset(final Map<String, String> map) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                Map<byte[], byte[]> value = Maps.newHashMap();

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    value.put(rawKey(entry.getKey()), rawKey(entry.getValue()));
                }
                connection.mSet(value);
                return true;
            }
        });
    }

    /**
     * @param key
     * @return
     */
    @Override
    public String get(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                return stringOf(connection.get(rawKey(key)));
            }
        });
    }

    @Override
    public List<String> mget(final Collection<String> keys) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public List<String> doInRedis(RedisConnection connection) {
                List<String> list = Lists.newArrayList();
                List<byte[]> values = connection.mGet(rawKeys(keys));
                if (values == null || values.isEmpty()) {
                    return list;
                }
                for (byte[] v : values) {
                    list.add(stringOf(v));
                }
                return list;
            }
        });
    }

    @Override
    public byte[] getBytes(final String key) {
        return (byte[]) redisTemplate.execute(new RedisCallback() {
            @Override
            public byte[] doInRedis(RedisConnection connection) {
                return connection.get(rawKey(key));
            }
        });
    }

    /**
     * @param key
     * @return
     */
    @Override
    public boolean exists(final String key) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.exists(rawKey(key));
            }
        });
    }

    @Override
    public boolean exists(final String key, final String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {

                DataType type = connection.type(rawKey(key));

                if (type == DataType.HASH) {
                    return connection.hExists(rawKey(key), rawKey(value));
                } else if (type == DataType.ZSET) {
                    return connection.zScore(rawKey(key), rawKey(value)) != null;
                } else if (type == DataType.SET) {
                    return connection.sIsMember(rawKey(key), rawKey(value));
                }
                return false;
            }
        });
    }

    /**
     * @return
     */
    @Override
    public long dbSize() {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.dbSize();
            }
        });
    }

    /**
     * @return
     */
    @Override
    public String ping() {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                return connection.ping();
            }
        });
    }

    @Override
    public Set<String> keys(final String pattern) {
        return (Set<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) {
                Set<String> list = Sets.newHashSet();
                Set<byte[]> values = connection.keys(rawKey(pattern));
                if (values == null) {
                    return list;
                }
                for (byte[] v : values) {
                    list.add(stringOf(v));
                }
                return list;
            }
        });
    }

    @Override
    public boolean sadd(final String key, final String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.sAdd(rawKey(key), rawKey(value)) > 0;
            }
        });
    }

    @Override
    public String spop(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                return stringOf(connection.sPop(rawKey(key)));
            }
        });
    }

    @Override
    public boolean zadd(final String key, final String value, final double score) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.zAdd(rawKey(key), score, rawKey(value));
            }
        });
    }

    @Override
    public long lpush(final String key, final String value) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.lPush(rawKey(key), rawKey(value));
            }
        });
    }

    @Override
    public long rpush(final String key, final String value) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.rPush(rawKey(key), rawKey(value));
            }
        });
    }

    @Override
    public String hget(final String key, final String field) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                return stringOf(connection.hGet(rawKey(key), rawKey(field)));
            }
        });
    }

    @Override
    public boolean hset(final String key, final String field, final String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.hSet(rawKey(key), rawKey(field), rawKey(value));
            }
        });
    }

    @Override
    public void hmset(final String key, final Map<String, String> map) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                if (map.isEmpty()) {
                    return false;
                }

                Map<byte[], byte[]> value = Maps.newHashMap();

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    value.put(rawKey(entry.getKey()), rawKey(entry.getValue()));
                }
                connection.hMSet(rawKey(key), value);

                return true;
            }
        });
    }

    @Override
    public List<String> hmget(final String key, final List<String> fields) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public List<String> doInRedis(RedisConnection connection) {

                List<String> list = Lists.newArrayList();

                List<byte[]> value = connection.hMGet(rawKey(key), rawKeys(fields));

                for (byte[] v : value) {
                    list.add(stringOf(v));
                }
                return list;
            }
        });
    }

    @Override
    public Map<String, String> hgetAll(final String key) {
        return (Map<String, String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Map<String, String> doInRedis(RedisConnection connection) {

                Map<String, String> map = Maps.newHashMap();

                Map<byte[], byte[]> value = connection.hGetAll(rawKey(key));

                if (value == null || value.isEmpty()) {
                    return map;
                }

                for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
                    map.put(stringOf(entry.getKey()), stringOf(entry.getValue()));
                }

                return map;
            }
        });
    }

    @Override
    public void hincrby(final String key, final String filed, final int value) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                connection.hIncrBy(rawKey(key), rawKey(filed), value);
                return true;
            }
        });
    }

    @Override
    public void hincrbyIfExists(final String key, final String filed, final int value) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                byte[] k = rawKey(key);
                if (!connection.exists(k)) {
                    return false;
                }
                connection.hIncrBy(k, rawKey(filed), value);
                return true;
            }
        });
    }

    @Override
    public List<String> sort(final String key) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public List<String> doInRedis(RedisConnection connection) {

                List<String> list = Lists.newArrayList();

                DataType type = connection.type(rawKey(key));

                if (type == DataType.LIST || type == DataType.SET || type == DataType.ZSET) {
                    List<byte[]> values = connection.sort(rawKey(key), null);

                    if (values == null || values.isEmpty()) {
                        return list;
                    }
                    for (byte[] value : values) {
                        list.add(stringOf(value));
                    }
                }
                return list;
            }
        });
    }

    @Override
    public long size(final String key) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {

                DataType type = connection.type(rawKey(key));
                if (type == DataType.LIST) {
                    return connection.lLen(rawKey(key));
                } else if (type == DataType.HASH) {
                    return connection.hLen(rawKey(key));
                } else if (type == DataType.ZSET) {
                    return connection.zCard(rawKey(key));
                } else if (type == DataType.SET) {
                    return connection.sCard(rawKey(key));
                }
                return 0L;
            }
        });
    }

    @Override
    public List<String> toList(final String key, final long begin, final long end) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public List<String> doInRedis(RedisConnection connection) {
                List<String> list = Lists.newArrayList();

                DataType type = connection.type(rawKey(key));

                Collection<byte[]> col = null;
                if (type == DataType.LIST) {
                    col = connection.lRange(rawKey(key), begin, end);
                } else if (type == DataType.ZSET) {
                    col = connection.zRange(rawKey(key), begin, end);
                } else if (type == DataType.SET) {
                    col = connection.sMembers(rawKey(key));
                }
                if (col != null && col.size() > 0) {
                    for (byte[] v : col) {
                        list.add(stringOf(v));
                    }
                }
                return list;
            }
        });
    }

    @Override
    public Set<String> toSet(final String key, final long begin, final long end) {
        return (Set<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) {
                Set<String> set = Sets.newHashSet();

                DataType type = connection.type(rawKey(key));

                Collection<byte[]> col = null;
                if (type == DataType.LIST) {
                    col = connection.lRange(rawKey(key), begin, end);
                } else if (type == DataType.ZSET) {
                    col = connection.zRange(rawKey(key), begin, end);
                } else if (type == DataType.SET) {
                    col = connection.sMembers(rawKey(key));
                }
                if (col != null && col.size() > 0) {
                    for (byte[] v : col) {
                        set.add(stringOf(v));
                    }
                }
                return set;
            }
        });
    }

    @Override
    public List<String> toList(final String key) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public List<String> doInRedis(RedisConnection connection) {
                List<String> list = Lists.newArrayList();
                DataType type = connection.type(rawKey(key));

                Collection<byte[]> col = null;
                if (type == DataType.LIST) {
                    col = connection.lRange(rawKey(key), 0, -1);
                } else if (type == DataType.ZSET) {
                    col = connection.zRange(rawKey(key), 0, -1);
                } else if (type == DataType.SET) {
                    col = connection.sMembers(rawKey(key));
                }
                if (col != null && col.size() > 0) {
                    for (byte[] v : col) {
                        list.add(stringOf(v));
                    }
                }
                return list;
            }
        });
    }

    @Override
    public Set<String> toSet(final String key) {
        return (Set<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) {
                Set<String> set = Sets.newHashSet();
                DataType type = connection.type(rawKey(key));

                Collection<byte[]> col = null;
                if (type == DataType.LIST) {
                    col = connection.lRange(rawKey(key), 0, -1);
                } else if (type == DataType.ZSET) {
                    col = connection.zRange(rawKey(key), 0, -1);
                } else if (type == DataType.SET) {
                    col = connection.sMembers(rawKey(key));
                }

                if (col != null && col.size() > 0) {
                    for (byte[] v : col) {
                        set.add(stringOf(v));
                    }
                }
                return set;
            }
        });
    }

    @Override
    public boolean del(final String key, final String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {

                DataType type = connection.type(rawKey(key));
                if (type == DataType.LIST) {
                    return connection.lRem(rawKey(key), 0, rawKey(value)) > 0;
                } else if (type == DataType.HASH) {
                    return connection.hDel(rawKey(key), rawKey(value)) > 0;
                } else if (type == DataType.ZSET) {
                    return connection.zRem(rawKey(key), rawKey(value)) > 0;
                } else if (type == DataType.SET) {
                    return connection.sRem(rawKey(key), rawKey(value)) > 0;
                }
                return true;
            }
        });
    }

    @Override
    public List<String> zrange(final String key, final long begin, final long end, final Order order) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public List<String> doInRedis(RedisConnection connection) {
                List<String> list = Lists.newArrayList();
                if (order == Order.DESC) {
                    Set<byte[]> set = connection.zRevRange(rawKey(key), begin, end);

                    if (set != null && set.size() > 0) {
                        for (byte[] v : set) {
                            list.add(stringOf(v));
                        }
                    }
                } else {
                    Set<byte[]> set = connection.zRange(rawKey(key), begin, end);

                    if (set != null && set.size() > 0) {
                        for (byte[] v : set) {
                            list.add(stringOf(v));
                        }
                    }
                }
                return list;
            }
        });
    }


    @Override
    public List<String> zrangeByScore(final String key, final long count, final double min, final double max, final Order order) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public List<String> doInRedis(RedisConnection connection) {
                List<String> list = Lists.newArrayList();
                if (order == Order.DESC) {
                    Set<byte[]> set = connection.zRevRangeByScore(rawKey(key), min, max, 0, count);

                    if (set != null && set.size() > 0) {
                        for (byte[] v : set) {
                            list.add(stringOf(v));
                        }
                    }
                } else {
                    Set<byte[]> set = connection.zRangeByScore(rawKey(key), min, max, 0, count);

                    if (set != null && set.size() > 0) {
                        for (byte[] v : set) {
                            list.add(stringOf(v));
                        }
                    }
                }
                return list;
            }
        });
    }

    @Override
    public List<String> zrange(String key, long begin, long end) {
        return zrange(key, begin, end, Order.ASC);
    }

    @Override
    public double zscore(final String key, final String value) {
        return (Double) redisTemplate.execute(new RedisCallback() {
            @Override
            public Double doInRedis(RedisConnection connection) {
                Double res = connection.zScore(rawKey(key), rawKey(value));
                return res == null ? 0d : res;
            }
        });
    }

    @Override
    public double zincrby(final String key, final String value, final double addv) {
        return (Double) redisTemplate.execute(new RedisCallback() {
            @Override
            public Double doInRedis(RedisConnection connection) {
                return connection.zIncrBy(rawKey(key), addv, rawKey(value));
            }
        });
    }

    @Override
    public String lpop(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                byte[] v = connection.lPop(rawKey(key));
                return stringOf(v);
            }
        });
    }

    @Override
    public String rpop(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public String doInRedis(RedisConnection connection) {
                byte[] v = connection.rPop(rawKey(key));
                return stringOf(v);
            }
        });
    }

    @Override
    public boolean lock(String key, String value, long timeOut) {
        return setNX(key, value, timeOut);
    }

    @Override
    public boolean unlock(String key, String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                byte[] lockKey = rawKey(key);
                byte[] v = connection.get(lockKey);
                if (stringOf(v).equals(value)) {
                    connection.del(lockKey);
                }
                return true;
            }
        });
    }

    @Override
    public boolean tryLock(String key, String value, long timeOut) {
        return setNX(key, value, timeOut);
    }

    @Override
    public long incr(final String key) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.incr(rawKey(key));
            }
        });
    }

    @Override
    public long incrBy(String key, long step) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.incrBy(rawKey(key), step);
            }
        });
    }

    @Override
    public long decr(String key) {
        return (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.decr(rawKey(key));
            }
        });
    }


    /**
     * 加锁
     *
     * @param locaName       锁的key
     * @param acquireTimeout 获取超时时间
     * @param timeout        锁的超时时间
     * @return 锁标识
     */
    @Override
    public boolean lockWithTimeout(String locaName,
                                   long acquireTimeout, long timeout) {
        try {
            // 锁名，即key值
            String lockKey = "LK:" + locaName;
            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquireTimeout;
            String nowTime = String.valueOf(System.currentTimeMillis());
            while (System.currentTimeMillis() < end) {
                if (redisTemplate.opsForValue().setIfAbsent(lockKey, nowTime)) {
                    redisTemplate.expire(lockKey, timeout, TimeUnit.MILLISECONDS);
                    // 返回value值，用于释放锁时间确认
                    return true;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
                if (redisTemplate.getExpire(lockKey) == -1) {
                    redisTemplate.expire(lockKey, timeout, TimeUnit.MILLISECONDS);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockName 锁的key
     * @return
     */
    @Override
    public void releaseLock(String lockName) {
        String lockKey = "LK:" + lockName;
        redisTemplate.delete(lockKey);
    }
}
