package fun.werfamily.sequence.snowflake.buffer;

import fun.werfamily.sequence.snowflake.util.NamingThreadFactory;
import fun.werfamily.sequence.snowflake.util.PaddedAtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@Slf4j
public class BufferPaddingExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RingBuffer.class);
    private static final String WORKER_NAME = "RingBuffer-Padding-Worker";
    private static final String SCHEDULE_NAME = "RingBuffer-Padding-Schedule";
    /**
     * 5 minutes
     **/
    private static final long DEFAULT_SCHEDULE_INTERVAL = 5 * 60L;
    private final AtomicBoolean running;
    private final PaddedAtomicLong lastSecond;
    private final RingBuffer ringBuffer;
    private final BufferedUidProvider uidProvider;
    private final ExecutorService bufferPadExecutors;
    private final ScheduledExecutorService bufferPadSchedule;
    private long scheduleInterval = DEFAULT_SCHEDULE_INTERVAL;

    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider) {
        this(ringBuffer, uidProvider, true);
    }

    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider, boolean usingSchedule) {
        this.running = new AtomicBoolean(false);
        this.lastSecond = new PaddedAtomicLong(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        this.ringBuffer = ringBuffer;
        this.uidProvider = uidProvider;
        int cores = Runtime.getRuntime().availableProcessors();

        bufferPadExecutors = new ThreadPoolExecutor(cores * 2, cores * 3, 2000,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(2048), new NamingThreadFactory(WORKER_NAME),
                new ThreadPoolExecutor.AbortPolicy());

        if (usingSchedule) {
            bufferPadSchedule = new ScheduledThreadPoolExecutor(1,
                    new NamingThreadFactory(SCHEDULE_NAME));
        } else {
            bufferPadSchedule = null;
        }
    }

    public void start() {
        if (bufferPadSchedule != null) {
            bufferPadSchedule.scheduleWithFixedDelay(() -> paddingBuffer(), scheduleInterval, scheduleInterval, TimeUnit.SECONDS);
        }
    }

    public void shutdown() {
        if (!bufferPadExecutors.isShutdown()) {
            bufferPadExecutors.shutdownNow();
        }

        if (bufferPadSchedule != null && !bufferPadSchedule.isShutdown()) {
            bufferPadSchedule.shutdownNow();
        }
    }

    /**
     * multi thread will trigger this
     **/
    public void asyncPadding() {
        bufferPadExecutors.submit(this::paddingBuffer);
    }

    public void paddingBuffer() {
        log.info("Ready to padding buffer lastSecond:{}. {}", lastSecond.get(), ringBuffer);
        if (!running.compareAndSet(false, true)) {
            log.info("Padding buffer is still running. {}", ringBuffer);
            return;
        }

        /** fill the rest slots until to catch the cursor **/
        boolean isFullRingBuffer = false;
        while (!isFullRingBuffer) {
            List<Long> uidList = uidProvider.provide(lastSecond.incrementAndGet());
            for (Long uid : uidList) {
                isFullRingBuffer = !ringBuffer.put(uid);
                if (isFullRingBuffer) {
                    break;
                }
            }
        }
        running.compareAndSet(true, false);
        log.info("End to padding buffer lastSecond:{}. {}", lastSecond.get(), ringBuffer);
    }

    public void setScheduleInterval(long scheduleInterval) {
        Assert.isTrue(scheduleInterval > 0, "Schedule interval must positive!");
        this.scheduleInterval = scheduleInterval;
    }

}
