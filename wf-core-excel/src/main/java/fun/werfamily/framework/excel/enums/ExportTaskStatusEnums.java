package fun.werfamily.framework.excel.enums;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/5/29.
 */
public enum ExportTaskStatusEnums {

    /**
     * 失败
     */
    FAIL(0, "失败"),
    /**
     * 导出中
     */
    ING(1, "进行中"),
    /**
     * 已完成
     */
    SUCCESS(2, "已完成"),
    /**
     * 已过期
     */
    EXPIRE(3, "已过期"),
    ;

    public final Integer val;
    public final String desc;

    ExportTaskStatusEnums(Integer val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public static ExportTaskStatusEnums getEnumByVal(Integer val) {
        for (ExportTaskStatusEnums item :
                ExportTaskStatusEnums.values()) {
            if (item.val.equals(val)) {
                return item;
            }
        }
        return null;
    }

    public Integer getVal() {
        return val;
    }

    public String getDesc() {
        return desc;
    }

}
