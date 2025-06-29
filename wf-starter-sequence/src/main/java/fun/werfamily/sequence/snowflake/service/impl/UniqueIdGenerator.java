package fun.werfamily.sequence.snowflake.service.impl;

import fun.werfamily.sequence.enums.UidServiceEnum;
import fun.werfamily.sequence.exception.UniqueIdGeneratorException;
import fun.werfamily.sequence.snowflake.config.BaseUidConfig;
import fun.werfamily.sequence.snowflake.service.UidGenerator;
import lombok.extern.slf4j.Slf4j;

/**
 * 遵循分布式id生成器snowflake算法
 * 以毫秒的精度递增
 *
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@Slf4j
public class UniqueIdGenerator extends BaseUidConfig implements UidGenerator {

    /**
     * 2019-01-14
     **/
    private final long twepoch = 1515898354000L;

    /**
     * machine id number
     */
    private final long workerIdBits = 7L;

    /**
     * data center number
     */
    private final long datacenterIdBits = 4L;

    /**
     * max machine id, the max is 31
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * max data center number, the max is 31
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * sequence id bits
     */
    private final long sequenceBits = 12L;

    /**
     * mchine ID left 12 bits
     */
    private final long workerIdShift = sequenceBits;

    /**
     * data center id, left shift 21 bit
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * timeStamp left shift 25)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * worker machine Id: 512
     */
    private long workerId;

    /**
     * data center 16
     */
    private long datacenterId;

    /**
     * milli sequence (0~4095)
     */
    private long sequence = 0L;

    /**
     * time stamp
     */
    private long lastTimestamp = -1L;


    /**
     * cannot exceed workerId and datacenterId
     *
     */
    public UniqueIdGenerator() {
//        if (workerId > maxWorkerId || workerId < 0) {
//            throw new UniqueIdGeneratorException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
//        }
//        if (datacenterId > maxDatacenterId || datacenterId < 0) {
//            throw new UniqueIdGeneratorException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
//        }
//        this.workerId = workerId;
//        this.datacenterId = datacenterId;
    }

    /**
     * get the next sequence id
     *
     * @return SnowflakeId
     */
    @Override
    public synchronized long getUID() {
        long timestamp = timeGen();

        /**time go back, do not support currently **/
        if (timestamp < lastTimestamp) {
            throw new UniqueIdGeneratorException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return allocate((timestamp - twepoch), workerId, sequence);
    }

    @Override
    public String getBizUID(UidServiceEnum uidServiceEnum) {
        throw new UniqueIdGeneratorException("unsupported");
    }

    /**
     * @param lastTimestamp lastTimestamp
     * @return current time stamp
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * return current time
     *
     * @return current time(milli seconds)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

}

