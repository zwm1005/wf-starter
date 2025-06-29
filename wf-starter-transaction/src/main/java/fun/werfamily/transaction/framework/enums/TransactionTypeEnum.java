package fun.werfamily.transaction.framework.enums;

/**
 * 事务类型枚举
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public enum TransactionTypeEnum {

    /**
     * 异常冲正型
     */
    REVERSAL("异常冲正型"),
    /**
     * 努力确保型
     */
    INSURE("努力确保型");

    private String desc;

    private TransactionTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
