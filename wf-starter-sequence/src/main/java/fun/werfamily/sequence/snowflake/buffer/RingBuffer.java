package fun.werfamily.sequence.snowflake.buffer;

import fun.werfamily.sequence.snowflake.util.PaddedAtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@Slf4j
public class RingBuffer {

    private static final int START_POINT = -1;
    private static final long CAN_PUT_FLAG = 0L;
    private static final long CAN_TAKE_FLAG = 1L;
    public static final int DEFAULT_PADDING_PERCENT = 50;
    private final int bufferSize;
    private final long indexMask;
    private final long[] slots;
    private final PaddedAtomicLong[] flags;
    private final AtomicLong tail = new PaddedAtomicLong(START_POINT);
    private final AtomicLong cursor = new PaddedAtomicLong(START_POINT);
    private final int paddingThreshold;
    private RejectedPutBufferHandler rejectedPutHandler = this::discardPutBuffer;
    private RejectedTakeBufferHandler rejectedTakeHandler = this::exceptionRejectedTakeBuffer;
    private BufferPaddingExecutor bufferPaddingExecutor;


    public RingBuffer(int bufferSize) {
        this(bufferSize, DEFAULT_PADDING_PERCENT);
    }


    public RingBuffer(int bufferSize, int paddingFactor) {
        // check buffer size is positive & a power of 2; padding factor in (0, 100)
        Assert.isTrue(bufferSize > 0L, "RingBuffer size must be positive");
        Assert.isTrue(Integer.bitCount(bufferSize) == 1, "RingBuffer size must be a power of 2");
        Assert.isTrue(paddingFactor > 0 && paddingFactor < 100, "RingBuffer size must be positive");

        this.bufferSize = bufferSize;
        this.indexMask = bufferSize - 1;
        this.slots = new long[bufferSize];
        this.flags = initFlags(bufferSize);

        this.paddingThreshold = bufferSize * paddingFactor / 100;
    }


    /**
     * put the uid with synchronized
     **/
    public synchronized boolean put(long uid) {
        long currentTail = tail.get();
        long currentCursor = cursor.get();
        long distance = currentTail - (currentCursor == START_POINT ? 0 : currentCursor);
        if (distance == bufferSize - 1) {
            rejectedPutHandler.rejectPutBuffer(this, uid);
            return false;
        }

        int nextTailIndex = calSlotIndex(currentTail + 1);
        if (flags[nextTailIndex].get() != CAN_PUT_FLAG) {
            rejectedPutHandler.rejectPutBuffer(this, uid);
            return false;
        }

        slots[nextTailIndex] = uid;
        flags[nextTailIndex].set(CAN_TAKE_FLAG);
        tail.incrementAndGet();
        return true;
    }

    /**
     * get the uid
     **/
    public long take() {
        long currentCursor = cursor.get();
        long nextCursor = cursor.updateAndGet(old -> old == tail.get() ? old : old + 1);
        Assert.isTrue(nextCursor >= currentCursor, "Curosr can't move back");
        long currentTail = tail.get();
        if (currentTail - nextCursor < paddingThreshold) {
            log.info("Reach the padding threshold:{}. tail:{}, cursor:{}, rest:{}", paddingThreshold, currentTail,
                    nextCursor, currentTail - nextCursor);
            bufferPaddingExecutor.asyncPadding();
        }
        if (nextCursor == currentCursor) {
            rejectedTakeHandler.rejectTakeBuffer(this);
        }

        int nextCursorIndex = calSlotIndex(nextCursor);
        Assert.isTrue(flags[nextCursorIndex].get() ==CAN_TAKE_FLAG, "Curosr not in can take status");
        long uid = slots[nextCursorIndex];
        flags[nextCursorIndex].set(CAN_PUT_FLAG);
        return uid;
    }


    protected int calSlotIndex(long sequence) {
        return (int) (sequence & indexMask);
    }

    protected void discardPutBuffer(RingBuffer ringBuffer, long uid) {
        log.warn("Rejected putting buffer for uid:{}. {}", uid, ringBuffer);
    }

    protected void exceptionRejectedTakeBuffer(RingBuffer ringBuffer) {
        log.warn("Rejected take buffer. {}", ringBuffer);
        throw new RuntimeException("Rejected take buffer. " + ringBuffer);
    }

    /**
     * Initialize flags asMr.WenMingN_PUT_FLAG
     */
    private PaddedAtomicLong[] initFlags(int bufferSize) {
        PaddedAtomicLong[] flags = new PaddedAtomicLong[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            flags[i] = new PaddedAtomicLong(CAN_PUT_FLAG);
        }

        return flags;
    }

    /**
     * Getters
     */
    public long getTail() {
        return tail.get();
    }

    public long getCursor() {
        return cursor.get();
    }

    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Setters
     */
    public void setBufferPaddingExecutor(BufferPaddingExecutor bufferPaddingExecutor) {
        this.bufferPaddingExecutor = bufferPaddingExecutor;
    }

    public void setRejectedPutHandler(RejectedPutBufferHandler rejectedPutHandler) {
        this.rejectedPutHandler = rejectedPutHandler;
    }

    public void setRejectedTakeHandler(RejectedTakeBufferHandler rejectedTakeHandler) {
        this.rejectedTakeHandler = rejectedTakeHandler;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RingBuffer [bufferSize=").append(bufferSize)
                .append(", tail=").append(tail)
                .append(", cursor=").append(cursor)
                .append(", paddingThreshold=").append(paddingThreshold).append("]");

        return builder.toString();
    }

}
