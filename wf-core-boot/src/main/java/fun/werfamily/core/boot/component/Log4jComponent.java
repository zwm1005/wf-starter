package fun.werfamily.core.boot.component;

import fun.werfamily.core.boot.load.LoadComponent;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2022/3/1 15:49
 */
public class Log4jComponent implements LoadComponent {


    @Override
    public void configService(SpringApplicationBuilder builder) {
        //do nothing
    }

    @Override
    public void afterComplete(ConfigurableApplicationContext context) {
        //do nothing
    }
}
