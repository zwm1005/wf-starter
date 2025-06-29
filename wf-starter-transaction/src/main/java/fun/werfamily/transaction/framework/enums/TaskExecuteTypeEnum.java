package fun.werfamily.transaction.framework.enums;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2022/11/1 15:03
 */
public enum TaskExecuteTypeEnum {
    /**
     * 立即执行
     */
    IMMEDIATELY("立即执行"),
    /**
     * 等待定时器扫描执行
     */
    LATER("稍后执行");

    private String desc;

    private TaskExecuteTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
