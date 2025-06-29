package fun.werfamily.sequence.snowflake.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import fun.werfamily.sequence.enums.UidServiceEnum;
import fun.werfamily.sequence.exception.UniqueIdGeneratorException;
import fun.werfamily.sequence.snowflake.buffer.BufferPaddingExecutor;
import fun.werfamily.sequence.snowflake.buffer.RejectedPutBufferHandler;
import fun.werfamily.sequence.snowflake.buffer.RejectedTakeBufferHandler;
import fun.werfamily.sequence.snowflake.buffer.RingBuffer;
import fun.werfamily.sequence.snowflake.config.BaseUidConfig;
import fun.werfamily.sequence.snowflake.service.UidGenerator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 百度分布式唯一ID生成器UidGenerator
 * 解决了时钟回拨问题
 *
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@Slf4j
public class CachedUidGenerator extends BaseUidConfig implements UidGenerator {
    private static final int DEFAULT_BOOST_POWER = 3;
    private int boostPower = DEFAULT_BOOST_POWER;
    private int paddingFactor = RingBuffer.DEFAULT_PADDING_PERCENT;
    private Long scheduleInterval;

    @Setter
    private RejectedPutBufferHandler rejectedPutBufferHandler;

    @Setter
    private RejectedTakeBufferHandler rejectedTakeBufferHandler;

    /**
     * RingBuffer
     */
    private RingBuffer ringBuffer;
    private BufferPaddingExecutor bufferPaddingExecutor;

    public CachedUidGenerator() {
    }

    @PostConstruct
    @Override
    public void configInit() {
        /** initialize workerId **/
        super.configInit();
        this.initRingBuffer();
        log.info("Initialized RingBuffer successfully.");
    }

    /**
     * 获取业务UID
     *
     * @param uidServiceEnum
     * @return
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Override
    public String getBizUID(UidServiceEnum uidServiceEnum) {
        long uid = getUID();
        long current = System.currentTimeMillis();
        String currentDateString = DatePattern.PURE_DATE_FORMAT.format(current);
        return currentDateString + uidServiceEnum.getValue() + uid;
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Override
    public long getUID() {
        try {
            return ringBuffer.take();
        } catch (Exception e) {
            log.error("Generate unique id exception. ", e);
            throw new UniqueIdGeneratorException();
        }
    }

    /**
     * parse uid
     **/
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public String parseUID(long uid) {
        long sequence = uid - (uid >> (seqBits) << seqBits);
        long workerId = (uid - ((uid >> (workerBits + seqBits)) << (workerBits + seqBits))) >> seqBits;
        long deltaSeconds = uid >>> (workerBits + seqBits);
        Date thatTime = new Date(TimeUnit.SECONDS.toMillis(epochSeconds + deltaSeconds));
        String thatTimeStr = DateUtil.formatDateTime(thatTime);
        // format as string
        return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                uid, thatTimeStr, workerId, sequence);
    }

    @PreDestroy
    public void destroy() throws Exception {
        bufferPaddingExecutor.shutdown();
    }

    protected List<Long> nextIdsForOneSecond(long currentSecond) {
        // Initialize result list size of (max sequence + 1)
        int listSize = (int) maxSequence + 1;
        List<Long> uidList = new ArrayList<>(listSize);
        long firstSeqUid = allocate(currentSecond - epochSeconds, workerId, 0L);
        for (int offset = 0; offset < listSize; offset++) {
            uidList.add(firstSeqUid + offset);
        }

        return uidList;
    }

    /**
     * init ringbuffer with configuration and thread padding
     */
    private void initRingBuffer() {
        int bufferSize = ((int) maxSequence + 1) << boostPower;
        this.ringBuffer = new RingBuffer(bufferSize, paddingFactor);
        log.info("Initialized ring buffer size:{}, paddingFactor:{}", bufferSize, paddingFactor);
        boolean usingSchedule = (scheduleInterval != null);
        this.bufferPaddingExecutor = new BufferPaddingExecutor(ringBuffer, this::nextIdsForOneSecond, usingSchedule);
        if (usingSchedule) {
            bufferPaddingExecutor.setScheduleInterval(scheduleInterval);
        }

        log.info("Initialized BufferPaddingExecutor. Using schdule:{}, interval:{}", usingSchedule, scheduleInterval);
        this.ringBuffer.setBufferPaddingExecutor(bufferPaddingExecutor);
        if (rejectedPutBufferHandler != null) {
            this.ringBuffer.setRejectedPutHandler(rejectedPutBufferHandler);
        }
        if (rejectedTakeBufferHandler != null) {
            this.ringBuffer.setRejectedTakeHandler(rejectedTakeBufferHandler);
        }

        bufferPaddingExecutor.paddingBuffer();
        bufferPaddingExecutor.start();
    }

    public void setBoostPower(int boostPower) {
        Assert.isTrue(boostPower > 0, "Boost power must be positive!");
        this.boostPower = boostPower;
    }

    public void setScheduleInterval(long scheduleInterval) {
        Assert.isTrue(scheduleInterval > 0, "Schedule interval must positive!");
        this.scheduleInterval = scheduleInterval;
    }

}
