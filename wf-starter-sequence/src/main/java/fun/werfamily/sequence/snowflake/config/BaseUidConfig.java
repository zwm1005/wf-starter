package fun.werfamily.sequence.snowflake.config;


import fun.werfamily.sequence.SequenceAutoConfiguration;
import fun.werfamily.sequence.exception.UniqueIdGeneratorException;
import fun.werfamily.sequence.snowflake.util.WorkerIdAssigner;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * +------+----------------------+----------------+-----------+
 * | sign |     delta seconds    | worker node id | sequence  |
 * +------+----------------------+----------------+-----------+
 * 1bit          40bits              17bits         6bits
 *
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
public class BaseUidConfig {

    protected int signBits = 1;
    protected int timeBits = 40;
    protected int workerBits = 17;
    protected int seqBits = 6;
    protected long sequence = 0L;
    protected long lastSecond = -1L;
    protected long maxDeltaSeconds = ~(-1L << timeBits);
    protected long maxWorkerId = ~(-1L << workerBits);
    protected long maxSequence = ~(-1L << seqBits);
    protected long timestampShift = workerBits + seqBits;
    protected long workerIdShift = seqBits;
    /**
     * 原64 bit
     **/
    public static final int TOTAL_BITS = 41;

    /**
     * application should config redisson instance to generate workerId
     **/
    @Autowired
    protected volatile RedissonClient redissonClient;

    @Autowired
    private SequenceAutoConfiguration sequenceAutoConfiguration;

    protected volatile long workerId;

    private ReentrantLock reentrantLock = new ReentrantLock();

    protected long epochSeconds = TimeUnit.MILLISECONDS.toSeconds(1648177788373L);

    public void configInit() {
        reentrantLock.lock();
        try {
            String bizCode = sequenceAutoConfiguration.getBizCode();
            if (StringUtils.isBlank(bizCode)) {
                throw new UniqueIdGeneratorException("fun.werfamily.sequence.bizCode 参数为必填项，请检查配置");
            }
            workerId = WorkerIdAssigner.getWorkerId(redissonClient, bizCode);
            if (workerId > maxWorkerId) {
                throw new RuntimeException("Worker id " + workerId + " exceeds the max " + maxWorkerId);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * UUID generator
     *
     * @param deltaSeconds
     * @param workerId
     * @param sequence
     * @return UUID
     */
    public long allocate(long deltaSeconds, long workerId, long sequence) {
        if (deltaSeconds > maxDeltaSeconds) {
            throw new UniqueIdGeneratorException("超出限定计算范围限制");
        }
        return (deltaSeconds << timestampShift) | (workerId << workerIdShift) | sequence;
    }
}
