package fun.werfamily.transaction.framework.commons;

import fun.werfamily.transaction.framework.enums.TaskExecuteErrorCodeEnum;

/**
 * 事务框架执行过程中的异常定义
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public class TransactionFrameworkException extends RuntimeException {

    private static final long serialVersionUID = -6378775606942426548L;

    private final TaskExecuteErrorCodeEnum errorCode;

    private final String errorDesc;

    public TransactionFrameworkException(TaskExecuteErrorCodeEnum errorCode, String errorDesc) {
        super();
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public TaskExecuteErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }


}
