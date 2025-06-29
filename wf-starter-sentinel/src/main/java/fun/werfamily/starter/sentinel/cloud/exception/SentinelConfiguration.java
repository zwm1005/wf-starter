package fun.werfamily.starter.sentinel.cloud.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2022/2/23 11:44
 */
@Configuration
@ConfigurationProperties(prefix = "wf.sentinel")
public class SentinelConfiguration {

    private String blockedMessage;

    @Bean
    public SentinelExceptionHandler buildExceptionHandler() {
        return new SentinelExceptionHandler();
    }

    public String getBlockedMessage() {
        return StringUtils.isEmpty(blockedMessage) ? "Blocked by Sentinel(rate limit)" : blockedMessage;
    }

    public void setBlockedMessage(String blockedMessage) {
        this.blockedMessage = blockedMessage;
    }
}
