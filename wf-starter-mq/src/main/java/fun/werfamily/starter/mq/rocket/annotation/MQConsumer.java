package fun.werfamily.starter.mq.rocket.annotation;

import fun.werfamily.starter.mq.rocket.base.MessageExtConst;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Description: RocketMQ消费者自动装配注解
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/1.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MQConsumer {
    String consumerGroup();

    String topic();

    /**
     * 广播模式消费： BROADCASTING
     * 集群模式消费： CLUSTERING
     *
     * @return 消息模式
     */
    String messageMode() default MessageExtConst.MESSAGE_MODE_CLUSTERING;

    /**
     * 使用线程池并发消费: CONCURRENTLY("CONCURRENTLY"),
     * 单线程消费: ORDERLY("ORDERLY");
     *
     * @return 消费模式
     */
    String consumeMode() default MessageExtConst.CONSUME_MODE_CONCURRENTLY;

    String[] tag() default {"*"};
}
