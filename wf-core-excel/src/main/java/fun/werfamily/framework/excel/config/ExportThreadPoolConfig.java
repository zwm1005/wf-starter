package fun.werfamily.framework.excel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
@Configuration
public class ExportThreadPoolConfig {

    @Value("${wf.core.export.async.executor.thread.core-pool-size:10}")
    private int corePoolSize;
    @Value("${wf.core.export.async.executor.thread.max-pool-size:40}")
    private int maxPoolSize;
    @Value("${wf.core.export.async.executor.thread.queue-capacity:400}")
    private int queueCapacity;

    @Bean("exprotTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置线程池参数信息
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("exprotTaskExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
//        taskExecutor.setAwaitTerminationSeconds(60);
        //修改拒绝策略为使用当前线程执行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化线程池
        taskExecutor.initialize();
        return taskExecutor;
    }

}
