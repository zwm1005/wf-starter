package fun.werfamily.starter.cache.service;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis 的操作开放接口
 */
public interface RedisService {

    /**
     * 通过key删除
     *
     * @param key
     */
    void del(String key);

    /**
     * 通过keys删除
     */
    void del(Collection<String> keys);

    /**
     * 给key设置生存时间
     *
     * @param key
     * @param timeOut 单位 秒
     * @return
     */
    boolean expire(String key, long timeOut);

    /**
     * 获取还剩多少存货时间
     *
     * @param key
     * @return
     */
    long ttl(String key);

    /**
     * 给key增加1
     *
     * @param key
     * @return
     */
    long incr(String key);

    /**
     * 增加指定步长
     * @param key
     * @param step
     * @return
     */
    long incrBy(String key, long step);

    /**
     * key减1
     * @param key
     * @return
     */
    long decr(String key);

    /**
     * 添加key value 并且设置存活时间
     *
     * @param key
     * @param value
     * @param timeOut 单位秒
     */
    void set(String key, String value, long timeOut);

    /**
     * 添加key value
     *
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * 添加key value
     *
     * @param key
     * @param value
     */
    void set(String key, byte[] value);

    /**
     * 添加key value
     *
     * @param key
     * @param value
     */
    void set(String key, byte[] value, long timeOut);

    /**
     * getAndSet
     *
     * @param key
     * @param value
     * @return
     */
    String getAndSet(String key, String value);

    /**
     * getAndSet
     *
     * @param key
     * @param value
     * @param timeOut
     * @return
     */
    String getAndSet(String key, String value, long timeOut);

    /**
     * 当key不存在时设置
     *
     * @param key
     * @param value
     */
    boolean setNX(String key, String value);

    /**
     * 当key不存在时设置
     *
     * @param key
     * @param value
     */
    boolean setNX(String key, String value, long timeOut);

    /**
     * 获取redis value (String)
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 获取多个key的value值
     *
     * @param keys
     * @return
     */
    List<String> mget(Collection<String> keys);

    /**
     * @param keys
     * @return
     */
    void mset(Map<String, String> keys);

    /**
     * 获取redis value (String)
     *
     * @param key
     * @return
     */
    byte[] getBytes(String key);

    /**
     * 通过正则匹配keys
     *
     * @param pattern
     * @return
     */
    Set<String> keys(String pattern);

    /**
     * 检查key是否已经存在
     *
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * 检查集合是否存在数据
     *
     * @param key
     * @param value
     * @return
     */
    boolean exists(String key, String value);



    /**
     * 查看redis里有多少数据
     */
    long dbSize();

    /**
     * 检查是否连接成功
     *
     * @return
     */
    String ping();

    /**
     * set add
     *
     * @param key
     * @param value
     * @return
     */

    boolean sadd(String key, String value);

    /**
     * 从set集合中随机删除返回一个
     *
     * @param key
     * @return
     */
    String spop(String key);

    /**
     * zset
     *
     * @param key
     * @param value
     * @return
     */
    boolean zadd(String key, String value, double score);

    /**
     * list
     *
     * @param key
     * @param value
     * @return
     */
    long lpush(String key, String value);

    /**
     * list
     *
     * @param key
     * @param value
     * @return
     */
    long rpush(String key, String value);

    /**
     * list
     *
     * @param key
     * @return
     */
    String lpop(String key);

    /**
     * list
     *
     * @param key
     * @return
     */
    String rpop(String key);

    /**
     * map
     *
     * @param key
     * @param field
     * @return
     */
    String hget(String key, String field);

    /**
     * map
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    boolean hset(String key, String field, String value);

    /**
     * map
     *
     * @param key
     * @param map
     */
    void hmset(String key, Map<String, String> map);

    /**
     * map
     *
     * @param key
     * @param fields
     * @return
     */
    List<String> hmget(String key, List<String> fields);

    /**
     * map值加1
     *
     * @param key
     * @param filed
     * @param value
     */
    void hincrby(String key, String filed, int value);

    /**
     * map值加1
     *
     * @param key
     * @param filed
     * @param value
     */
    void hincrbyIfExists(String key, String filed, int value);

    /**
     * map
     *
     * @param key
     * @return
     */
    Map<String, String> hgetAll(String key);

    /**
     * 获取list,set,zset的所有数据对象,里面的值只能是数字
     *
     * @param key
     * @return
     */
    List<String> sort(String key);

    /**
     * 获取集合数据的数量
     *
     * @param key
     * @return
     */
    long size(String key);

    /**
     * 获取集合元素某个,适应zset,list获取分页,set获取全部
     *
     * @param key
     * @param begin
     * @param end
     * @return
     */
    List<String> toList(String key, long begin, long end);

    /**
     * 获取集合元素某个,适应zset,list,去重,set获取全部
     *
     * @param key
     * @param begin
     * @param end
     * @return
     */
    Set<String> toSet(String key, long begin, long end);

    /**
     * 获取集合,结果list，适应于list,set,zset
     *
     * @param key
     * @return
     */
    List<String> toList(String key);

    /**
     * 获取集合,结果set,可去重，适应于list,set,zset
     *
     * @param key
     * @return
     */
    Set<String> toSet(String key);

    /**
     * 删除集合元素
     *
     * @param key
     * @param value
     * @return
     */
    boolean del(String key, String value);

    /**
     * zset排序集合获取
     *
     * @param key
     * @param begin
     * @param end
     * @param order
     * @return
     */
    List<String> zrange(String key, long begin, long end, Order order);

    /**
     * zset排序集合获取,默认升序
     *
     * @param key
     * @param begin
     * @param end
     * @return
     */
    List<String> zrange(String key, long begin, long end);


    /**
     * 返回集合中指定区间的元素
     *
     * @param key
     * @param count
     * @param min
     * @param max
     * @param order
     * @return
     */
    List<String> zrangeByScore(String key, long count, double min, double max, Order order);

    /**
     * 获取zset的score值
     *
     * @param key
     * @param value
     * @return
     */
    double zscore(String key, String value);

    /**
     * zset值加指定值
     *
     * @param key
     * @param value
     * @return
     */
    double zincrby(String key, String value, double addv);

    /**
     * 开始锁
     *
     * @param key
     * @param value
     * @param timeOut
     * @return
     */
    boolean lock(String key, String value, long timeOut);

    /**
     * 尝试获取锁
     *
     * @param key
     * @param value
     * @param timeOut
     * @return
     */
    boolean tryLock(String key, String value, long timeOut);

    /**
     * 解锁
     *
     * @param key
     * @param value
     * @return
     */
    boolean unlock(String key, String value);


    /**
     * 加锁
     *
     * @param locaName       锁的key
     * @param acquireTimeout 获取超时时间
     * @param timeout        锁的超时时间
     * @return 锁标识
     */
    boolean lockWithTimeout(String locaName,
                            long acquireTimeout, long timeout);

    /**
     * 释放锁
     *
     * @param lockName 锁的key
     * @return
     */
    void releaseLock(String lockName);

}