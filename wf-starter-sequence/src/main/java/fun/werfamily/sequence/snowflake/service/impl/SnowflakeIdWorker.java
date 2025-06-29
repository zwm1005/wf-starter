package fun.werfamily.sequence.snowflake.service.impl;

import cn.hutool.core.date.DatePattern;
import fun.werfamily.sequence.SequenceAutoConfiguration;
import fun.werfamily.sequence.enums.UidBusinessCode;
import fun.werfamily.sequence.enums.UidServiceEnum;
import fun.werfamily.sequence.exception.UniqueIdGeneratorException;
import fun.werfamily.sequence.snowflake.service.UidGenerator;
import fun.werfamily.sequence.snowflake.util.WorkerIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Calendar;

/**
 * @Author luofuchuan
 */
@Slf4j
public class SnowflakeIdWorker implements UidGenerator {

    /**
     * 机器id所占的位数,按应用名区分，同一应用最多布置15台机器
     */
    private final long workerIdBits = 4L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 3L;

    /**
     * 机器ID向左移位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 时间截向左移位
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits;

    /**
     * 生成序列的掩码，这里为15 (0b1111=15)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~15)
     */
    private long workerId;

    /**
     * 毫秒内序列(0~15)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    /**
     * 基准值(二进制:1000000000000000000000000000)
     */
    private long referenceValue = 134217728L;

    @Autowired
    private WorkerIdGenerate workerIdGenerate;

    @Autowired
    private SequenceAutoConfiguration sequenceAutoConfiguration;

    public SnowflakeIdWorker() {

    }

    /**
     * 构造函数
     *
     * @param workerId 工作ID (0~31)
     */
    public SnowflakeIdWorker(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    @PostConstruct
    protected void configInit() {
        String bizCode = sequenceAutoConfiguration.getBizCode();
        if (StringUtils.isBlank(bizCode)) {
            throw new UniqueIdGeneratorException("fun.werfamily.sequence.bizCode 参数为必填项，请检查配置");
        }
        this.workerId = workerIdGenerate.getWorkerId(bizCode);

        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
    }

    public void destoryWorkId() {
        workerIdGenerate.destroyWorkId();
    }


    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    protected synchronized ImmutablePair<Long, Long> nextId() {
        long timestamp = timeGen();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        long todayZero = getTodayZero(timestamp);
        long nextId = (((timestamp - todayZero) | referenceValue) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;

        return new ImmutablePair<>(nextId, timestamp);
    }

    @Override
    public synchronized long getUID() {
        ImmutablePair<Long, Long> nextIdAndTimeStamp = nextId();
        String currentDateString = DatePattern.PURE_DATE_FORMAT.format(nextIdAndTimeStamp.getValue()).substring(2);

        return Long.valueOf(currentDateString + nextIdAndTimeStamp.getKey());
    }

    @Override
    public synchronized String getBizUID(UidServiceEnum uidServiceEnum) {
        ImmutablePair<Long, Long> nextIdAndTimeStamp = nextId();
        String format = String.format("%010d", nextIdAndTimeStamp.getKey());
        String currentDateString = DatePattern.PURE_DATE_FORMAT.format(nextIdAndTimeStamp.getValue()).substring(2);

        return currentDateString + format + uidServiceEnum.getValue();
    }

    public synchronized String getBizUID(UidServiceEnum uidServiceEnum, UidBusinessCode uidBusinessCode) {
        if (uidBusinessCode.code() != null) {
            if (uidBusinessCode.code() < 0 || uidBusinessCode.code() > 9) {
                throw new UniqueIdGeneratorException("业务code超长清检查");
            }
        }
        ImmutablePair<Long, Long> nextIdAndTimeStamp = nextId();
        String format = String.format("%010d", nextIdAndTimeStamp.getKey());
        String currentDateString = DatePattern.PURE_DATE_FORMAT.format(nextIdAndTimeStamp.getValue()).substring(2);

        return currentDateString + format + uidServiceEnum.getValue() + uidBusinessCode.code();
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当天0点时间戳
     *
     * @param timestamp 当前时间戳
     * @return 当前时间(毫秒)
     */
    protected long getTodayZero(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }


    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }


}
