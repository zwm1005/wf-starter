package fun.werfamily.sequence.snowflake.buffer;

/**
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@FunctionalInterface
public interface RejectedPutBufferHandler {
    /**
     * rejectPutBuffer
     *
     * @param ringBuffer
     * @param uid
     */
    void rejectPutBuffer(RingBuffer ringBuffer, long uid);
}
