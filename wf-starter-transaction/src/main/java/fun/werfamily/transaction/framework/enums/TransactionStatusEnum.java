package fun.werfamily.transaction.framework.enums;

/**
 * 事务状态
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public enum TransactionStatusEnum {

    /**
     * 提交
     */
    COMMIT("提交"),
    /**
     * 回滚
     */
    ROLLBACK("回滚");

    private String desc;

    private TransactionStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
