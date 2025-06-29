package fun.werfamily.core.util.enums;

/**
 * Description: 日志级别类型
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/9.
 */
public enum WfLogType {
    /**
     * 日志类型
     */
    CONTROLLER("controller", "控制层"),
    CLIENT("client", "客户层"),
    SERVICE("Service", "服务层"),
    BIZ("biz", "业务逻辑层"),
    OTHER("other", "其他");

    public String value;
    public String desc;
    public String messageModel;

    WfLogType(String value, String desc) {
        this.value = value;
        this.desc = desc;
        this.messageModel = messageModel;
    }


}
