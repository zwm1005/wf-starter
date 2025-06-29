package fun.werfamily.transaction.framework.enums;

/**
 * @AuthorMr.WenMing
 */
public enum TaskExecuteErrorCodeEnum {
    /**
     * 任务持久化失败
     */
    PERSIST_FAILED("任务持久化失败"),
    /**
     * 重复的请求
     */
    REPEATED_REQUEST("重复的请求"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR("系统异常"),
    /**
     * 操作成功
     */
    SUCCESS("操作成功");

    private final String desc;

    private TaskExecuteErrorCodeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
