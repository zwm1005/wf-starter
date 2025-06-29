package fun.werfamily.base.response;

/**
 * Description: 系统统一返回Code码
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2021/12/9.
 */
public enum SysResponseCode implements IResponseCode {
    /**
     * 系统统一返回Code码
     */
    OK(200, "处理成功", "处理成功"),
    ERROR_PARAM(400, "请求参数错误", "请求参数错误, ${detail}"),
    UNAUTHORIZED(401, "未授权", "未授权"),
    LOGIN_FAILED(411, "登录失败", "登录失败"),
    TOKEN_INVALID(412, "TOKEN失效", "未登录"),
    SERVER_ERROR(500, "系统内部错误", "系统内部错误"),
    TRIGGER_FUSE(505, "系统内部错误", "触发熔断"),
    SERVICE_UNAVAILABLE(503, "服务不可用", "系统内部错误"),
    SYNC_QUEUE_LOCK_ERROR(801, "系统繁忙", "获取分布式锁异常"),
    REPEAT_ERROR(802, "重复操作", "重复操作"),
    SYNC_QUEUE_LOCK_TIME_OUT(803, "系统繁忙", "获取锁超时"),
    ;

    private final int code;
    private final String friendlyMsg;
    private final String logMsg;

    @SuppressWarnings("unused")
    SysResponseCode(int code, String friendlyMsg) {
        this.code = code;
        this.friendlyMsg = friendlyMsg;
        this.logMsg = friendlyMsg;
    }

    SysResponseCode(int code, String friendlyMsg, String logMsg) {
        this.code = code;
        this.friendlyMsg = friendlyMsg;
        this.logMsg = logMsg;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String friendlyMsg() {
        return friendlyMsg;
    }

    @Override
    public String logMsg() {
        return logMsg;
    }

    @SuppressWarnings("unused")
    public static SysResponseCode codeOf(int code) {
        for (SysResponseCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid ResponseCode code: " + code);
    }

}
