package fun.werfamily.starter.mq.rocket.trace.dispatch;

/**
 * @Author alvin
 * @date 16-3-7
 * 数据编码和发送模块
 */
public abstract class AbstractAsyncAppender {
    /**
     * 编码数据上下文到缓冲区
     *
     * @param context 上下文
     */
    public abstract void append(Object context);

    /**
     * 实际写数据操作
     */
    public abstract void flush();
}
