package fun.werfamily.sequence.enums;


/**
 * @AuthorMr.WenMing
 */
public enum UidServiceEnum {
    /**
     * 领域编码 需要提前报备
     */
    ORDER("00", "order", "订单域"),
    USER("01", "user", "会员域"),
    OTHER("02", "other", "默认未分类"),
    ACCOUNT("03", "account", "账户域"),
    ;

    private String value;
    private String code;
    private String desc;

    UidServiceEnum(String value, String code, String desc) {
        this.value = value;
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
