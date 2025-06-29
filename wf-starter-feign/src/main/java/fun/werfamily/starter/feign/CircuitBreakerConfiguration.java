package fun.werfamily.starter.feign;


import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @Author: Mr.WenMing
 * @since: 2021/5/12
 */
@Configuration
@ConditionalOnProperty(value = "feign.circuitbreaker.enabled", matchIfMissing = true)
@EnableConfigurationProperties(CircuitBreakerProperties.class)
public class CircuitBreakerConfiguration {

    final CircuitBreakerProperties circuitBreakerProperties;

    public CircuitBreakerConfiguration(CircuitBreakerProperties circuitBreakerProperties) {
        this.circuitBreakerProperties = circuitBreakerProperties;
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(circuitBreakerProperties.getTimeLimiter().getTimeOutMills()))
                .build();
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig).build());
    }

}