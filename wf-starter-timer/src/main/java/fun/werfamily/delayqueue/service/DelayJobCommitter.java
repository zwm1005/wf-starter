package fun.werfamily.delayqueue.service;

import fun.werfamily.delayqueue.job.DelayJob;
import fun.werfamily.delayqueue.timer.JobTimer;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Mr.WenMing
 * @since: 2021/8/23
 */
public class DelayJobCommitter implements Serializable {

    private static final long serialVersionUID = 5473420647772214501L;

    @Autowired
    private RedissonClient client;

    /**
     * @param job      待执行任务
     * @param delay    延迟
     * @param timeUnit 单位
     */
    public void commit(DelayJob job, Long delay, TimeUnit timeUnit) {
        RBlockingQueue blockingQueue = client.getBlockingQueue(JobTimer.JOBS_TAG);
        RDelayedQueue delayedQueue = client.getDelayedQueue(blockingQueue);
        delayedQueue.offer(job, delay, timeUnit);
    }

}