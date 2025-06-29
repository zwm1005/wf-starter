package fun.werfamily.starter.mq.rocket.shutdown;

import fun.werfamily.starter.mq.rocket.annotation.MQConsumer;
import fun.werfamily.starter.mq.rocket.base.AbstractMQPushConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @AuthorMr.WenMing
 */
@Component
@Slf4j
public class MQConsumerShutdown implements SmartInitializingSingleton {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, Object> mqConsumers;

    public void shutdownAllMQConsumers() {
        for (Map.Entry<String, Object> entry : mqConsumers.entrySet()) {
            try {
                AbstractMQPushConsumer<?> consumerBean = (AbstractMQPushConsumer<?>) entry.getValue();
                consumerBean.getConsumer().shutdown();
                log.info("RocketMQEndpoint：rocketMq消费者监听器关闭成功.{}", consumerBean.getClass().getName());
            } catch (Exception e) {
                log.error("RocketMQEndpoint：rocketMq消费者监听器关闭失败.", e);
            }
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        mqConsumers = applicationContext.getBeansWithAnnotation(MQConsumer.class);
    }
}

