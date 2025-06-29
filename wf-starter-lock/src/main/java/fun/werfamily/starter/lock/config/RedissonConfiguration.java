package fun.werfamily.starter.lock.config;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2022/2/28 13:51
 */
@Configuration
@AutoConfigureAfter(value = RedissonClient.class)
public class RedissonConfiguration {

}
