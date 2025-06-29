package fun.werfamily.transaction.framework.test;

import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.TaskExecuteStatusEnum;
import fun.werfamily.transaction.framework.task.TransactionTask;
import org.junit.platform.commons.util.StringUtils;

import java.sql.Timestamp;

/**
 * task builder helper
 *
 * @AuthorMr.WenMing
 */
class TaskBuilderUtils {
    private static TransactionTaskLogDO createTaskDo(TransactionTask task, String status, String retryStatus) {
        return createTaskDoWithCustomTime(task, 6, status, retryStatus);
    }

    private static TransactionTaskLogDO createTaskDoWithCustomTime(TransactionTask task, int minuteBefore, String status, String retryStatus) {
        TransactionTaskLogDO logDo = new TransactionTaskLogDO();
        logDo.setTaskId(task.getTaskId());
        logDo.setTaskType(task.getTaskType());
        logDo.setTaskClassName(task.getClass().getTypeName());
        if (!StringUtils.isBlank(retryStatus)) {
            logDo.setRetryStatus(retryStatus);
        }
        logDo.setStatus(status);
        logDo.setTransactionType(task.getTransactionType().name());
        logDo.setTimes(1);
        logDo.setRequestAdditionalInfo(task.serializeAdditionalInfo());
        long dateMinuteBefore = System.currentTimeMillis() - 60000L * minuteBefore;
        logDo.setCreateTime(new Timestamp(dateMinuteBefore));
        logDo.setUpdateTime(new Timestamp(dateMinuteBefore));
        logDo.setNextExecuteTime(new Timestamp(dateMinuteBefore));
        logDo.setRequestAdditionalInfo(task.serializeAdditionalInfo());
        return logDo;
    }

    public static TransactionTaskLogDO longTimeProcessingTask(TransactionTask task) {
        return createTaskDo(task, TaskExecuteStatusEnum.PROCESSING.name(), null);
    }

    public static TransactionTaskLogDO longTimeProcessingOvertime(TransactionTask task) {
        return createTaskDoWithCustomTime(task, 61, TaskExecuteStatusEnum.PROCESSING.name(), null);
    }

    public static TransactionTaskLogDO retryTask(TransactionTask task) {
        return createTaskDo(task, TaskExecuteStatusEnum.EXCEPTION.name(), "WAIT_RETRY");
    }

    public static TransactionTaskLogDO retryOvertime(TransactionTask task) {
        return createTaskDoWithCustomTime(task, 61, TaskExecuteStatusEnum.EXCEPTION.name(), "WAIT_RETRY");
    }
}
