package fun.werfamily.transaction.framework.enums;


/**
 * 任务执行状态
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public enum TaskRetryStatusEnum {
    /**
     * 待重试
     */
    WAIT_RETRY("待重试"),
    /**
     * 重试完成
     */
    RETRY_FINISHED("重试完成"),
    /**
     * 重试超过最大限制
     */
    RETRY_ULTRA_LIMIT("重试超过最大限制"),
    /**
     * 无需重试
     */
    NO_RETRY("无需重试");

    private String desc;

    private TaskRetryStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


}
