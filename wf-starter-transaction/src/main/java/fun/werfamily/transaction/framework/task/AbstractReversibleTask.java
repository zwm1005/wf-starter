package fun.werfamily.transaction.framework.task;

import fun.werfamily.transaction.framework.enums.TaskRetryStrategyEnum;
import fun.werfamily.transaction.framework.enums.TransactionStatusEnum;
import fun.werfamily.transaction.framework.enums.TransactionTypeEnum;

import java.sql.Timestamp;

/**
 * 异常冲正型任务
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public abstract class AbstractReversibleTask<T extends TaskExecuteResult> implements TransactionTask<T> {

    @Override
    public String getTaskType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public final TransactionTypeEnum getTransactionType() {
        return TransactionTypeEnum.REVERSAL;
    }


    @Override
    public TaskRetryStrategyEnum getRetryStrategy() {
        return TaskRetryStrategyEnum.INCREASING_INTERVAL;
    }

    @Override
    public String serializeAdditionalInfo() {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * 执行异常冲正
     *
     * @return
     */
    public abstract T doReversal();


    /**
     * 查询业务执行状态
     * 当远程任务执行成功，但长期处于未提交状态，框架会尝试自动恢复，
     * 此时会回调该方法，查询当前业务是要提交还是回滚
     *
     * @return
     */
    public abstract TransactionStatusEnum queryBizStatus();

    @Override
    public Timestamp getFirstExecuteTime(){
        return null;
    }
}
