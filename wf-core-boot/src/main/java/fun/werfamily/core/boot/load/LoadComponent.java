package fun.werfamily.core.boot.load;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * 启动扩展
 *
 * @Author: Mr.WenMing
 * @since: 2021/5/13
 */
public interface LoadComponent extends Ordered, Comparable<LoadComponent> {

    /**
     * 启动处理器
     *
     * @param builder spring build
     */
    void configService(SpringApplicationBuilder builder);

    /**
     * 启动完成后 回调
     *
     * @param context spring context
     */
    void afterComplete(ConfigurableApplicationContext context);

    /**
     * 获取排列顺序
     *
     * @return order
     */
    @Override
    default int getOrder() {
        return 0;
    }

    /**
     * 对比排序
     *
     * @param o LauncherService
     * @return compare
     */
    @Override
    default int compareTo(LoadComponent o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}

