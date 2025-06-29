package fun.werfamily.transaction.framework.executor.impl;

import fun.werfamily.transaction.framework.commons.TransactionFrameworkException;
import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.TaskExecuteErrorCodeEnum;
import fun.werfamily.transaction.framework.enums.TaskExecuteStatusEnum;
import fun.werfamily.transaction.framework.mapper.TransactionTaskLogMapper;
import fun.werfamily.transaction.framework.task.TaskExecuteResult;
import fun.werfamily.transaction.framework.task.TransactionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public class TransactionTaskManager {

    private static Logger log = LoggerFactory.getLogger(TransactionTaskManager.class);

    public TransactionTaskLogMapper enterpriseTaskMapper;

    public TransactionTaskManager(TransactionTaskLogMapper enterpriseTaskMapper) {
        this.enterpriseTaskMapper = enterpriseTaskMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateTransactionTaskLog(TransactionTaskLogDO taskDo) {
        // 3 持久化执行结果
        enterpriseTaskMapper.update(taskDo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public <T extends TaskExecuteResult> TransactionTaskLogDO persistTaskForNewTransaction(TransactionTask<T> task) {
        return this.persistTask(task);
    }

    /**
     * @param <T>
     * @param task
     * @return
     */
    public <T extends TaskExecuteResult> TransactionTaskLogDO persistTask(TransactionTask<T> task) {
        TransactionTaskLogDO logDo = new TransactionTaskLogDO();
        logDo.setTaskId(task.getTaskId());
        logDo.setTaskType(task.getTaskType());
        logDo.setTaskClassName(task.getClass().getTypeName());
        logDo.setStatus(TaskExecuteStatusEnum.PROCESSING.name());
        logDo.setTransactionType(task.getTransactionType().name());
        logDo.setTimes(1);
        logDo.setRequestAdditionalInfo(task.serializeAdditionalInfo());
        logDo.setNextExecuteTime(task.getFirstExecuteTime());

        try {
            enterpriseTaskMapper.save(logDo);
        } catch (DuplicateKeyException e) {
            log.error("任务已存在，请不要重复提交={}", logDo);
            throw new TransactionFrameworkException(TaskExecuteErrorCodeEnum.REPEATED_REQUEST, "任务重复，请勿重复提交");
        } catch (Exception e) {
            log.error("任务持久化失败={}", logDo, e);
            throw new TransactionFrameworkException(TaskExecuteErrorCodeEnum.PERSIST_FAILED,
                    TaskExecuteErrorCodeEnum.PERSIST_FAILED.getDesc());
        }
        return logDo;
    }
}
