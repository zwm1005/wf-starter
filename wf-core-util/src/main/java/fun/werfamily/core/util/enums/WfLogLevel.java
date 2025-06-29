package fun.werfamily.core.util.enums;

/**
 * Description: 日志级别
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/9.
 */
public enum WfLogLevel {
    /**
     * 日志级别
     */
    INFO("info", "正常", "{0} class：{1}  method：{2} params：{3}  result {4}"),
    DEBUG("debug", "调试", "{0} class：{1}  method：{2} params：{3}  result {4}"),
    WARN("warn", "警告", "{0} class：{1}  method：{2} params：{3}  result {4}"),
    ERROR("error", "错误", " {0} class：{1}  method：{2} params：{3}  result：{4} traceId：{5}");

    public String value;
    public String desc;
    public String messageModel;

    WfLogLevel(String value, String desc, String messageModel) {
        this.value = value;
        this.desc = desc;
        this.messageModel = messageModel;
    }


}
