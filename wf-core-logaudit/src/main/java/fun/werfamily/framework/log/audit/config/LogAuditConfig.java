package fun.werfamily.framework.log.audit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
@Configuration
@ConfigurationProperties("wf.core.logaudit")
@Data
public class LogAuditConfig {
    /**
     * 服务名
     */
    private String ServiceName;


}
