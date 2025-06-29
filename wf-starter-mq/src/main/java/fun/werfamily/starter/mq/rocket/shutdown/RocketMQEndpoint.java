package fun.werfamily.starter.mq.rocket.shutdown;

import fun.werfamily.starter.mq.rocket.base.WfDefaultMQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2024/3/8 10:41
 */
@Component
@Endpoint(id = "rocketmq")
@Slf4j
public class RocketMQEndpoint implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Autowired
    private WfDefaultMQProducer producer;

    @Autowired
    private MQConsumerShutdown mqConsumerShutdown;

    @ReadOperation
    public String health() {
        return "RocketMQ health";
    }

    @WriteOperation
    public void shutdown() {
        try {
            if (null != producer) {
                producer.shutdown();
            }
            log.info("RocketMQEndpoint：rocketMq生产者监听器关闭成功. {}", producer);
        } catch (Exception e) {
            log.error("RocketMQEndpoint：rocketMq生产者监听器关闭失败.", e);
        }

        try {
            mqConsumerShutdown.shutdownAllMQConsumers();
            log.info("RocketMQEndpoint：rocketMq消费者监听器关闭成功.");
        } catch (Exception e) {
            log.error("RocketMQEndpoint：rocketMq消费者监听器关闭失败.", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
