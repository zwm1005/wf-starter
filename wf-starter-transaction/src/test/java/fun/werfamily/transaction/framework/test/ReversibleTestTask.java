package fun.werfamily.transaction.framework.test;

import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.TaskExecuteStatusEnum;
import fun.werfamily.transaction.framework.enums.TaskRetryStrategyEnum;
import fun.werfamily.transaction.framework.enums.TransactionStatusEnum;
import fun.werfamily.transaction.framework.task.AbstractReversibleTask;
import fun.werfamily.transaction.framework.task.TaskExecuteResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 测试task
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public class ReversibleTestTask extends AbstractReversibleTask<TaskExecuteResult> {

    private TaskExecuteResult result;

    private String taskId = "TEST-" + RandomStringUtils.randomAlphanumeric(9);

    public ReversibleTestTask() {

    }

    public ReversibleTestTask(TaskExecuteResult result, String taskId) {
        this.result = result;
        if (StringUtils.isNotBlank(taskId)) {
            this.taskId = taskId;
        }
    }

    public TaskExecuteResult getResult() {
        return result;
    }

    public void setResult(TaskExecuteResult result) {
        this.result = result;
    }

    @Override
    public String getTaskType() {
        return "TEST";
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public TaskRetryStrategyEnum getRetryStrategy() {
        return TaskRetryStrategyEnum.INCREASING_INTERVAL;
    }

    @Override
    public TaskExecuteResult doExecute() {
        return result;
    }

    @Override
    public String serializeAdditionalInfo() {
        return null;
    }

    @Override
    public TaskExecuteResult doReversal() {
        TaskExecuteResult result = new TaskExecuteResult();
        result.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        return result;
    }

    @Override
    public TransactionStatusEnum queryBizStatus() {
        return TransactionStatusEnum.COMMIT;
    }

    @Override
    public void rebuild(TransactionTaskLogDO taskDo) {
        this.taskId = taskDo.getTaskId();
        TaskExecuteResult result = new TaskExecuteResult();
        result.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        this.result = result;
    }
}
