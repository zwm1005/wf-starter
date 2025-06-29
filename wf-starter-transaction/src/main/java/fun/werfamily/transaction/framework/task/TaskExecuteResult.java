package fun.werfamily.transaction.framework.task;

import fun.werfamily.transaction.framework.commons.AbstractPrintable;
import fun.werfamily.transaction.framework.enums.TaskExecuteStatusEnum;

/**
 * 任务执行结果，业务可扩展定义个性化的Result对象
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public class TaskExecuteResult extends AbstractPrintable {

    private static final long serialVersionUID = 7219686598944917201L;

    protected TaskExecuteStatusEnum executeStatus;

    protected String errorCode;

    protected String errorMessage;

    /**
     * 序列化
     *
     * @return
     */
    public String serialize() {
        return null;
    }

    public TaskExecuteStatusEnum getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(TaskExecuteStatusEnum executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
