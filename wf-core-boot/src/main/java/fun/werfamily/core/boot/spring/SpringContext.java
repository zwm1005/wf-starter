package fun.werfamily.core.boot.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * @Author: Mr.WenMing
 * @since: 2021/5/15
 */
@Component
@Lazy(false)
public class SpringContext implements ApplicationContextAware, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SpringContext.class);

    private static ApplicationContext applicationContext = null;

    private static ConfigurableApplicationContext configurableApplicationContext;

    public static ConfigurableApplicationContext getConfigurableApplicationContext() {
        return configurableApplicationContext;
    }

    public static void setConfigurableApplicationContext(
            ConfigurableApplicationContext configurableApplicationContext) {
        SpringContext.configurableApplicationContext = configurableApplicationContext;
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        try {
            if (Objects.isNull(applicationContext)) {
                synchronized (SpringContext.class) {
                    if (Objects.isNull(applicationContext)) {
                        logger.debug("wait SpringContextHolder init!");
                        SpringContext.class.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error("wait SpringContextHolder init error!", e);
        }
        return applicationContext;
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
        synchronized (SpringContext.class) {
            SpringContext.class.notifyAll();
            logger.info("init SpringContextHolder success!");
        }
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        if (name == null) {
            return null;
        }
        return Objects.nonNull(applicationContext) ? (T) applicationContext.getBean(name) : null;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        return Objects.nonNull(applicationContext) ? applicationContext.getBean(requiredType) : null;
    }

    /**
     * 清除SpringContext中的ApplicationContext为Null.
     */
    public static void clearContext() {
        applicationContext = null;
    }

    /**
     * 发布事件
     *
     * @param event spring event
     */
    public static void publishEvent(ApplicationEvent event) {
        Optional.ofNullable(getApplicationContext()).ifPresent(app -> app.publishEvent(event));
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() {
        SpringContext.clearContext();
    }

}
