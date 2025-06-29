package fun.werfamily.starter.nacos.component;

import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 关闭 nacos 服务自动注册 --spring.cloud.nacos.discovery.register-enabled=false
 * 用于devops健康检查通过后的服务后置注册
 *
 * @Author : Mr.WenMing
 * @create 2023/5/9 16:13
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.nacos.discovery.register-enabled", havingValue = "false")
public class NacosPostRegister implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(NacosPostRegister.class);

    private ApplicationContext applicationContext;

    /**
     * 服务注册入口
     */
    public void register() {
        NacosServiceRegistry nacosServiceRegistry = applicationContext.getBean(NacosServiceRegistry.class);
        Registration registration = applicationContext.getBean(Registration.class);
        nacosServiceRegistry.register(registration);
        logger.info("srv register successfully. {}", registration);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
