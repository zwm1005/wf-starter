package fun.werfamily.transaction.framework.job;

import fun.werfamily.transaction.framework.autoconfig.TransactionTaskProperties;
import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.*;
import fun.werfamily.transaction.framework.mapper.TransactionTaskLogMapper;
import fun.werfamily.transaction.framework.task.AbstractReversibleTask;
import fun.werfamily.transaction.framework.task.TaskExecuteResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 异常冲正型任务异常恢复job
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public class ReversibleTaskExceptionRecoverJob {

    /**
     * 最大循环次数， 防止系统数据量较大， 导致无限循环
     */
    private static final int MAX_LOOP_TIMES = 50;
    private static Logger log = LoggerFactory.getLogger(ReversibleTaskExceptionRecoverJob.class);
    private TransactionTaskLogMapper mapper;

    private TransactionTaskProperties taskExecutorProperties;

    public ReversibleTaskExceptionRecoverJob(TransactionTaskLogMapper mapper,
                                             TransactionTaskProperties taskExecutorProperties) {
        this.mapper = mapper;
        this.taskExecutorProperties = taskExecutorProperties;
    }

    @XxlJob(value = "ReversibleTaskExceptionRecoverJob")
    public ReturnT<String> execute(String args) {
        int hour = 1;
        if (!StringUtils.isEmpty(args)) {
            try {
                hour = Integer.parseInt(args);
                // min value 1 hour
                if (hour < 1) {
                    hour = 1;
                }
            } catch (Exception e) {
                log.warn("一致性任务处理xxljob传入参数异常， 将使用1小时作为时间线处理");
            }
        }

        boolean hasLongtimeProcessingTask = true;
        int loopTimes = 0;
        while (++loopTimes <= MAX_LOOP_TIMES) {
            List<TransactionTaskLogDO> taskList = new ArrayList<TransactionTaskLogDO>();

            // 先捞长期处理中的任务-》任务状态为处理中,成功，异常，冲正中且时间超过10分钟
            if (hasLongtimeProcessingTask) {
                taskList.addAll(
                        mapper.selectLongtimeProcessingTaskForReversal(taskExecutorProperties.getRetryFetchSize(), hour));
            }

            // 如果无长期处理中的异常任务，捞取任务状态为待重试且到重试时间的任务
            if (CollectionUtils.isEmpty(taskList) || taskList.size() < taskExecutorProperties.getRetryFetchSize()) {
                hasLongtimeProcessingTask = false;
                taskList.addAll(mapper.selectRetryTaskTaskForReversal(taskExecutorProperties.getRetryFetchSize(), hour));
            }

            if (CollectionUtils.isEmpty(taskList)) {
                return ReturnT.SUCCESS;
            }

            for (TransactionTaskLogDO item : taskList) {
                // 1. 重建任务对象
                try {
                    AbstractReversibleTask<?> task = rebuildTask(item);
                    if (task == null) {
                        return ReturnT.SUCCESS;
                    }

                    // 2. 判断事务是否需要提交，是提交事务并返回，否则走后续冲正
                    if (doSubmit(item, task)) {
                        return ReturnT.SUCCESS;
                    }

                    // 3. 执行冲正
                    this.doReversal(task, item);
                } catch (Exception e) {
                    log.warn("任务执行异常,后续会再次重试，item={}", item, e);
                }
            }
        }
        return ReturnT.SUCCESS;

    }

    /**
     * 如果任务执行成功但为未提交状态, 回查业务确定是否要提交，需要提交的执行提交
     *
     * @param item
     * @param task
     * @return
     */
    private boolean doSubmit(TransactionTaskLogDO item, AbstractReversibleTask<?> task) {
        if (StringUtils.equals(item.getStatus(), TaskExecuteStatusEnum.SUCCESS.name())) {
            TransactionStatusEnum bizStatus = task.queryBizStatus();
            // 业务执行成功，更新状态为已提交状态, 否则执行冲正
            if (bizStatus == TransactionStatusEnum.COMMIT) {
                List<Long> list = new ArrayList<Long>(1);
                list.add(item.getId());
                mapper.batchUpdateStatus(list, TaskExecuteStatusEnum.COMMITED.name(),
                        TaskExecuteStatusEnum.SUCCESS.name());
                return true;
            }
        }
        return false;
    }

    /**
     * 重建任务对象，如果重建失败，大概率是系统Bug, 不再重试
     *
     * @param item
     * @return
     */
    private AbstractReversibleTask<?> rebuildTask(TransactionTaskLogDO item) {
        try {
            AbstractReversibleTask<?> task = (AbstractReversibleTask<?>) Class.forName(item.getTaskClassName()).newInstance();
            task.rebuild(item);
            return task;
        } catch (Throwable e) {
            log.error("重建任务出现异常，任务将不会再次重试, task{}", item, e);
            item.setTimes(item.getTimes() + 1);
            item.setErrorCode(TaskExecuteErrorCodeEnum.SYSTEM_ERROR.name());
            item.setErrorMessage(StringUtils.left(e.getMessage(), 200));
            item.setReversalStatus(TaskReversalStatusEnum.REVERSAL_EXCEPTION.name());
            item.setRetryStatus(TaskRetryStatusEnum.NO_RETRY.name());
            mapper.update(item);
            return null;
        }

    }

    private void doReversal(AbstractReversibleTask task, TransactionTaskLogDO taskDo) {
        taskDo.setReversalStatus(TaskReversalStatusEnum.REVERSAL_ING.name());
        taskDo.setTimes(taskDo.getTimes() + 1);
        mapper.updateReversiStatus(taskDo);

        TaskExecuteResult result = new TaskExecuteResult();
        try {
            // 2.1 调用task执行任务
            result = task.doReversal();
        } catch (Throwable e) {
            log.error("远程任务执行异常{}", task, e);
            result.setExecuteStatus(TaskExecuteStatusEnum.EXCEPTION);
            result.setErrorCode(TaskExecuteErrorCodeEnum.SYSTEM_ERROR.name());
            result.setErrorMessage(StringUtils.left(e.getMessage(), 200));
        }

        // 解析任务结果
        TaskReversalStatusEnum status = TaskReversalStatusEnum.convert(result.getExecuteStatus());
        taskDo.setReversalStatus(status.name());
        taskDo.setResultAdditionalInfo(result.serialize());
        taskDo.setErrorCode(result.getErrorCode());
        taskDo.setErrorMessage(StringUtils.left(result.getErrorMessage(), 200));

        if (status.isEnd()) {
            taskDo.setRetryStatus(TaskRetryStatusEnum.RETRY_FINISHED.name());
        } else {
            log.error("冲正执行异常{}", result);
            if (taskDo.getTimes() + 1 > task.getRetryStrategy().getMaxRetryTimes()) {
                taskDo.setRetryStatus(TaskRetryStatusEnum.RETRY_ULTRA_LIMIT.name());
            } else {
                taskDo.setRetryStatus(TaskRetryStatusEnum.WAIT_RETRY.name());
                taskDo.setNextExecuteTime(
                        new Timestamp(task.getRetryStrategy().calNextExecuteTime(taskDo.getTimes()).getTime()));
            }
        }

        // 2.2 持久化执行结果
        mapper.updateReversiStatus(taskDo);
    }

}
