package fun.werfamily.starter.mq.rocket.trace.common;


/**
 * @Author alvin
 * @date 16-3-8
 */
public class OnsTraceConstants {
    /**
     * 外部直传Nameserver的地址
     */
    public static String NAMESRV_ADDR = "NAMESRV_ADDR";
    /**
     * 外部传入地址服务器的Url，获取NameServer地址
     */
    public static String ADDRSRV_URL = "ADDRSRV_URL";
    /**
     * 实例名称
     */
    public static final String INSTANCE_NAME = "InstanceName";
    /**
     * 缓冲区队列大小
     */
    public static final String ASYNC_BUFFER_SIZE = "AsyncBufferSize";
    /**
     * 最大Batch
     */
    public static final String MAX_BATCH_NUM = "MaxBatchNum";

    public static final String WAKE_UP_NUM = "WakeUpNum";
    /**
     * Batch消息最大大小
     */
    public static final String MAX_MSG_SIZE = "MaxMsgSize";
    public static final String GROUP_NAME = "_INNER_TRACE_PRODUCER";
    public static final String TRACE_TOPIC = "MQ_TRACE_DATA";
    public static char CONTENT_SPLITOR = (char) 1;
    public static char FIELD_SPLITOR = (char) 2;
}
