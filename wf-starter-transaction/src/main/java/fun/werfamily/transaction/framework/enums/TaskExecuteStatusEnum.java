package fun.werfamily.transaction.framework.enums;

/**
 * 事务日志状态
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public enum TaskExecuteStatusEnum {

    /**
     * 处理中
     */
    PROCESSING("处理中"),
    /**
     * 异常
     */
    EXCEPTION("异常"),
    /**
     * 失败
     */
    FAILED("失败"),
    /**
     * 成功
     */
    SUCCESS("成功"),
    /**
     * 已提交
     */
    COMMITED("已提交");

    private String desc;

    private TaskExecuteStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否终结状态
     *
     * @return
     */
    public boolean isEndForInsurableTask() {
        return this == SUCCESS || this == FAILED;
    }

}
	