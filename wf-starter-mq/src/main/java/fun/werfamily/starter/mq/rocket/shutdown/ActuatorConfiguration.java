package fun.werfamily.starter.mq.rocket.shutdown;

import fun.werfamily.starter.mq.rocket.annotation.EnableMQConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2024/3/8 10:41
 */
@Configuration
public class ActuatorConfiguration {

    @Bean
    @ConditionalOnBean(annotation = EnableMQConfiguration.class)
    public RocketMQEndpoint rocketMQEndpoint() {
        return new RocketMQEndpoint();
    }
}
