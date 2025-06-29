package fun.werfamily.framework.log.audit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.application")
@Data
public class ServiceNameDefaultConfig {
    /**
     * 服务名
     */
    private String name;
}
