package fun.werfamily.transaction.framework.task;

import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.TaskRetryStrategyEnum;
import fun.werfamily.transaction.framework.enums.TransactionTypeEnum;

import java.sql.Timestamp;

/**
 * 事务任务
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public interface TransactionTask<T extends TaskExecuteResult> {

    /**
     * 返回事务类型
     *
     * @return
     */
    TransactionTypeEnum getTransactionType();

    /**
     * 返回任务类型
     *
     * @return
     */
    String getTaskType();

    /**
     * 返回任务ID, 这个是任务的唯一标识
     *
     * @return
     */
    String getTaskId();

    /**
     * 返回重试策略
     *
     * @return
     */
    TaskRetryStrategyEnum getRetryStrategy();

    /**
     * 执行任务
     *
     * @return
     */
    T doExecute();


    /**
     * 序列化任务附加信息，主要用于rebuild时使用
     *
     * @return
     */
    String serializeAdditionalInfo();

    /**
     * 根据数据库里保存的任务信息， 重建任务对象， 这个方法里可填充任务执行过程中用到的属性信息
     *
     * @param logDo
     */
    void rebuild(TransactionTaskLogDO logDo);

    /**
     * 获取首次执行时间
     *
     * @return
     */
    Timestamp getFirstExecuteTime();
}
