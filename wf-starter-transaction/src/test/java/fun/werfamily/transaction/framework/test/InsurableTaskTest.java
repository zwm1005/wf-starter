package fun.werfamily.transaction.framework.test;

import fun.werfamily.transaction.framework.autoconfig.TransactionTaskProperties;
import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import fun.werfamily.transaction.framework.enums.TaskExecuteStatusEnum;
import fun.werfamily.transaction.framework.executor.InsurableTaskExecutor;
import fun.werfamily.transaction.framework.job.InsurableTaskExceptionRecoverJob;
import fun.werfamily.transaction.framework.mapper.TransactionTaskLogMapper;
import fun.werfamily.transaction.framework.task.TaskExecuteResult;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * insurable task for test
 *
 * @AuthorMr.WenMing
 */
public class InsurableTaskTest extends BaseTest {

    @Autowired
    TransactionTaskProperties config;
    @Autowired
    private TransactionTaskLogMapper taskMapper;
    @Autowired
    private InsurableTaskExecutor insurableTaskExecutor;

    @Autowired
    private InsurableTaskExceptionRecoverJob insurableTaskExceptionRecoverJob;

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
        InsurableTestTask task = new InsurableTestTask(mockResult, null);
        insurableTaskExecutor.execute(task);
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
        InsurableTestTask longTimeProcessingTask = new InsurableTestTask(longTimeTaskResult, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.longTimeProcessingTask(longTimeProcessingTask));

        //recover job
        ReturnT<String> executeResult = insurableTaskExceptionRecoverJob.execute(null);
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //long time task test
        TransactionTaskLogDO dbTask = taskMapper.selectByTaskId(longTimeProcessingTask.getTaskId(), longTimeProcessingTask.getTaskType());
        Assert.assertEquals(dbTask.getStatus(), TaskExecuteStatusEnum.SUCCESS.name());
    }

    @Test
    public void testRecoverLongTimeProcessingOvertime() {
        //long time processing status record generate
        TaskExecuteResult longTimeProcessingOver = new TaskExecuteResult();
        longTimeProcessingOver.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        InsurableTestTask longTimeProcessingOvertimeTask = new InsurableTestTask(longTimeProcessingOver, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.longTimeProcessingOvertime(longTimeProcessingOvertimeTask));

        //recover job
        ReturnT<String> executeResult = insurableTaskExceptionRecoverJob.execute(null);
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //long time task hour before test
        TransactionTaskLogDO hourBeforeDbTask = taskMapper.selectByTaskId(longTimeProcessingOvertimeTask.getTaskId(), longTimeProcessingOvertimeTask.getTaskType());
        Assert.assertEquals(hourBeforeDbTask.getStatus(), TaskExecuteStatusEnum.PROCESSING.name());

    }

    @Test
    public void testRetry() {
        TaskExecuteResult retryResult = new TaskExecuteResult();
        retryResult.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        InsurableTestTask retryTask = new InsurableTestTask(retryResult, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.retryTask(retryTask));

        //recover job
        ReturnT<String> executeResult = insurableTaskExceptionRecoverJob.execute(null);
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //retry time task test
        TransactionTaskLogDO retryTaskDo = taskMapper.selectByTaskId(retryTask.getTaskId(), retryTask.getTaskType());
        Assert.assertEquals(retryTaskDo.getStatus(), TaskExecuteStatusEnum.SUCCESS.name());
    }

    @Test
    public void testRetryOvertime() {
        TaskExecuteResult retryTaskOvertime = new TaskExecuteResult();
        retryTaskOvertime.setExecuteStatus(TaskExecuteStatusEnum.SUCCESS);
        InsurableTestTask retryTaskOvertimeTask = new InsurableTestTask(retryTaskOvertime, null);
        taskMapper.saveCustomTime(TaskBuilderUtils.retryOvertime(retryTaskOvertimeTask));

        //recover job
        ReturnT<String> executeResult = insurableTaskExceptionRecoverJob.execute("2");
        Assert.assertEquals(ReturnT.SUCCESS_CODE, executeResult.getCode());

        //retry time task test
        TransactionTaskLogDO retryTaskOvertimeTaskDo = taskMapper.selectByTaskId(retryTaskOvertimeTask.getTaskId(), retryTaskOvertimeTask.getTaskType());
        Assert.assertEquals(retryTaskOvertimeTaskDo.getStatus(), TaskExecuteStatusEnum.SUCCESS.name());
    }
}
