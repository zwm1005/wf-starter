package fun.werfamily.sequence.snowflake.config;

import fun.werfamily.sequence.snowflake.service.impl.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @Author luofuchuan
 */
@Component
@Slf4j
public class RedssonCloseIntercept implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        snowflakeIdWorker.destoryWorkId();
    }
}
