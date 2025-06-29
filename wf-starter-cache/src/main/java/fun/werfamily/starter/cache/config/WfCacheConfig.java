package fun.werfamily.starter.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/10/14.
 */
@Configuration
@ConfigurationProperties("wf.cache")
@Data
public class WfCacheConfig {
    /**
     * redis cache switch
     */
    private Boolean redisAnnoSwitch = true;


}
