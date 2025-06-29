package fun.werfamily.delayqueue.executor;

import fun.werfamily.delayqueue.job.DelayJob;

/**
 * @Author: Mr.WenMing
 * @since: 2021/8/23
 */
public interface JobExecutor<T extends DelayJob<P>, P> {

    /**
     * 任务执行入口
     *
     * @param job 待执行任务
     */
    void execute(T job);
}