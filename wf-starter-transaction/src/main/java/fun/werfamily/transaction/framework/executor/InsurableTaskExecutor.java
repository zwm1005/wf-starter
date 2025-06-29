package fun.werfamily.transaction.framework.executor;

import fun.werfamily.transaction.framework.task.AbstractInsurableTask;
import fun.werfamily.transaction.framework.task.TaskExecuteResult;

/**
 * 努力确保型任务执行器
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public interface InsurableTaskExecutor {

    /**
     * 执行努力确保型任务
     *
     * @param <T>
     * @param task
     * @return
     */
    public <T extends TaskExecuteResult> void execute(AbstractInsurableTask<T> task);


}
