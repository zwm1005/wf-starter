package fun.werfamily.starter.feign;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.bootstrap.BootstrapConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

/**
 * @Author: Mr.WenMing
 * @since: 2021/5/12
 */
@BootstrapConfiguration
@AutoConfigureBefore({FeignAutoConfiguration.class})
public class FeignPropertySourceConfig {

}
