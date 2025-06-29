package fun.werfamily.delayqueue;

import fun.werfamily.delayqueue.service.DelayJobCommitter;
import fun.werfamily.delayqueue.timer.JobTimer;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Mr.WenMing
 * @since: 2021/8/23
 */
@Configuration
@ConditionalOnClass({SpringBootApplication.class, RedissonClient.class})
public class DelayQueueAutoConfiguration {

    @Bean
    public JobTimer jobTimer() {
        return new JobTimer();
    }

    @Bean
    public DelayJobCommitter delayJobService() {
        return new DelayJobCommitter();
    }
}
