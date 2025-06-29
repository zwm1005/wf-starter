package fun.werfamily.sequence.snowflake.util;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

/**
 * @Author: Mr.WenMing
 * @since: 2021/8/22
 */
public class WorkerIdAssigner {
    private static final String UUID_GENERATOR_KEY = "uuid_generator";

    private WorkerIdAssigner() {
    }

    public static long getWorkerId(RedissonClient redisson, String bizCode) {
        RAtomicLong rAtomicLong = redisson.getAtomicLong(String.join(":", UUID_GENERATOR_KEY, bizCode));
        return rAtomicLong.incrementAndGet();
    }
}
