package fun.werfamily.transaction.framework.enums;

import fun.werfamily.transaction.framework.commons.TransactionFrameworkException;

/**
 * 任务冲正状态
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public enum TaskReversalStatusEnum {
    /**
     * 冲正中
     */
    REVERSAL_ING("冲正中"),
    /**
     * 冲正成功
     */
    REVERSAL_SUCCESS("冲正成功"),
    /**
     * 冲正失败
     */
    REVERSAL_FAILED("冲正失败"),
    /**
     * 冲正异常
     */
    REVERSAL_EXCEPTION("冲正异常");

    private String desc;

    private TaskReversalStatusEnum(String desc) {
        this.desc = desc;
    }

    public static TaskReversalStatusEnum convert(TaskExecuteStatusEnum status) {
        if (status == null) {
            return null;
        }

        if (status == TaskExecuteStatusEnum.SUCCESS) {
            return TaskReversalStatusEnum.REVERSAL_SUCCESS;
        }

        if (status == TaskExecuteStatusEnum.FAILED) {
            return TaskReversalStatusEnum.REVERSAL_FAILED;
        }

        if (status == TaskExecuteStatusEnum.EXCEPTION) {
            return TaskReversalStatusEnum.REVERSAL_EXCEPTION;
        }

        throw new TransactionFrameworkException(TaskExecuteErrorCodeEnum.SYSTEM_ERROR, "冲正结果状态无效");
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否终结状态
     *
     * @return
     */
    public boolean isEnd() {
        return this == REVERSAL_SUCCESS || this == REVERSAL_FAILED;
    }

}
