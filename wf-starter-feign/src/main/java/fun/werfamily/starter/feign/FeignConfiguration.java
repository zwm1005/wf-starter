package fun.werfamily.starter.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Mr.WenMing
 * @since: 2021/5/12
 */
@Configuration
@EnableFeignClients(basePackages = {"fun.werfamily"})
public class FeignConfiguration {

    @Bean
    FeignErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}

