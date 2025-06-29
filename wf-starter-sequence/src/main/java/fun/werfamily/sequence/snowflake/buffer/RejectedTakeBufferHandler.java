package fun.werfamily.sequence.snowflake.buffer;

/**
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@FunctionalInterface
public interface RejectedTakeBufferHandler {
    /**
     * rejectTakeBuffer
     *
     * @param ringBuffer
     */
    void rejectTakeBuffer(RingBuffer ringBuffer);
}
