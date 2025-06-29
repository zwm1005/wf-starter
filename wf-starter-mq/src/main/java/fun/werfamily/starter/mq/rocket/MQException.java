package fun.werfamily.starter.mq.rocket;

/**
 * @Author yipin
 * @date 2017/6/28
 * RocketMQ的自定义异常
 */
@SuppressWarnings("ALL")
public class MQException extends RuntimeException {
    public MQException(String msg) {
        super(msg);
    }
}
