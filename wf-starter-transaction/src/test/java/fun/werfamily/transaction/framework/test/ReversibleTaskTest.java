package fun.werfamily.transaction.framework.test;

import fun.werfamily.transaction.framework.autoconfig.TransactionTaskProperties;
import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.TaskExecuteStatusEnum;
import fun.werfamily.transaction.framework.enums.TaskReversalStatusEnum;
import fun.werfamily.transaction.framework.executor.ReversibleTaskExecutor;
import fun.werfamily.transaction.framework.job.ReversibleTaskExceptionRecoverJob;
import fun.werfamily.transaction.framework.mapper.TransactionTaskLogMapper;
import fun.werfamily.transaction.framework.task.TaskExecuteResult;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * test units for reversible task
 *
 * @AuthorMr.WenMing
 */
public class ReversibleTaskTest extends BaseTest {

    @Autowired
    TransactionTaskProperties config;
    @Autowired
    private TransactionTaskLogMapper taskMapper;
    @Autowired
    private ReversibleTaskExecutor executor;

    @Autowired
    private ReversibleTaskExceptionRecoverJob taskJob;

    /**
     * test insurable task execute
     * <p>
     * dont open transaction because spring unit test will break event transaction
     *
     * @throws InterruptedException cancelable
     * @throws ExecutionException   exception by framework
     */
    @Test
    public void testTask() throws InterruptedException, ExecutionException {
        //this test must disable delete
        config.setEnableImmediatelyDelete(false);

        TaskExecuteResult mockResult = new TaskExecuteResult();
        mockResult.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        ReversibleTestTask task = new ReversibleTestTask(mockResult, null);
        executor.execute(task);
        //ensure task execute
        Thread.sleep(1000);

        TransactionTaskLogDO taskDo = taskMapper.selectByTaskId(task.getTaskId(), task.getTaskType());
        Assert.assertEquals(task.getTaskId(), taskDo.getTaskId());
        Assert.assertEquals(task.getTaskType(), taskDo.getTaskType());
        Assert.assertNull(taskDo.getRetryStatus());
        Assert.assertEquals(taskDo.getStatus(), TaskExecuteStatusEnum.SUCCESS.name());
        Assert.assertEquals(1, taskDo.getTimes());
    }

    @Test
    public void testRecoverLongTimeProcessing() {
        //long time processing status record generate
        TaskExecuteResult longTimeTaskResult = new TaskExecuteResult();
        longTimeTaskResult.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        ReversibleTestTask longTimeProcessingTask = new ReversibleTestTask(longTimeTaskResult, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.longTimeProcessingTask(longTimeProcessingTask));

        //recover job
        ReturnT<String> executeResult = taskJob.execute(null);
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //long time task test
        TransactionTaskLogDO dbTask = taskMapper.selectByTaskId(longTimeProcessingTask.getTaskId(), longTimeProcessingTask.getTaskType());
        Assert.assertEquals(dbTask.getStatus(), TaskExecuteStatusEnum.PROCESSING.name());
    }

    @Test
    public void testRecoverLongTimeProcessingOvertime() {
        //long time processing status record generate
        TaskExecuteResult longTimeProcessingOver = new TaskExecuteResult();
        longTimeProcessingOver.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        ReversibleTestTask longTimeProcessingOvertimeTask = new ReversibleTestTask(longTimeProcessingOver, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.longTimeProcessingOvertime(longTimeProcessingOvertimeTask));

        //recover job
        ReturnT<String> executeResult = taskJob.execute(null);
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //long time task hour before test
        TransactionTaskLogDO hourBeforeDbTask = taskMapper.selectByTaskId(longTimeProcessingOvertimeTask.getTaskId(), longTimeProcessingOvertimeTask.getTaskType());
        Assert.assertEquals(hourBeforeDbTask.getStatus(), TaskExecuteStatusEnum.PROCESSING.name());

    }

    @Test
    public void testRetry() {
        TaskExecuteResult retryResult = new TaskExecuteResult();
        retryResult.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        ReversibleTestTask retryTask = new ReversibleTestTask(retryResult, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.retryTask(retryTask));

        //recover job
        ReturnT<String> executeResult = taskJob.execute(null);
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //retry time task test
        TransactionTaskLogDO retryTaskDo = taskMapper.selectByTaskId(retryTask.getTaskId(), retryTask.getTaskType());
        Assert.assertEquals(retryTaskDo.getReversalStatus(), TaskReversalStatusEnum.REVERSAL_SUCCESS.name());
    }

    @Test
    public void testRetryOvertime() {
        TaskExecuteResult retryTaskOvertime = new TaskExecuteResult();
        retryTaskOvertime.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        ReversibleTestTask retryTaskOvertimeTask = new ReversibleTestTask(retryTaskOvertime, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.retryOvertime(retryTaskOvertimeTask));

        //recover job
        ReturnT<String> executeResult = taskJob.execute("2");
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //retry time task test
        TransactionTaskLogDO retryTaskOvertimeTaskDo = taskMapper.selectByTaskId(retryTaskOvertimeTask.getTaskId(), retryTaskOvertimeTask.getTaskType());
        Assert.assertEquals(retryTaskOvertimeTaskDo.getStatus(), TaskExecuteStatusEnum.EXCEPTION.name());
    }
}
