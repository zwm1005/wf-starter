package fun.werfamily.core.boot;

import fun.werfamily.core.boot.load.LoadComponent;
import fun.werfamily.core.boot.spring.SpringContext;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * @Author: Mr.WenMing
 * @since: 2021/5/17
 */
public class WfApplication {

    /**
     * @param source main run class
     * @param args   command params
     * @return {@link ConfigurableApplicationContext}
     */
    public static ConfigurableApplicationContext run(Class<?> source, String... args) {
        // 加载自定义组件
        List<LoadComponent> loadServiceList = new ArrayList<>();
        ServiceLoader.load(LoadComponent.class).forEach(loadServiceList::add);
        List<LoadComponent> sortLoadServices = loadServiceList.stream()
                .sorted(Comparator.comparing(LoadComponent::getOrder)).collect(Collectors.toList());

        // spring load
        SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
        sortLoadServices.forEach(load -> load.configService(builder));
        ConfigurableApplicationContext configurableApplicationContext = builder.run(args);
        SpringContext.setConfigurableApplicationContext(configurableApplicationContext);

        sortLoadServices.forEach(load -> load.afterComplete(configurableApplicationContext));
        return configurableApplicationContext;
    }

}