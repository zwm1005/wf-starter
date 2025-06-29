package fun.werfamily.starter.mq.rocket.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * RocketMQ生产者自动装配注解
 *
 * @Author Mr.WenMing
 * Created on 2022/3/1.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MQProducer {
}
