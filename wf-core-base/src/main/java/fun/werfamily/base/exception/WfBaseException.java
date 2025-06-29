package fun.werfamily.base.exception;

import fun.werfamily.base.response.IResponseCode;
import fun.werfamily.base.util.ExceptionUtil;

import java.util.Map;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2021/12/8.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class WfBaseException extends RuntimeException {

    private IResponseCode code;
    private Map msgParams = null;

    public WfBaseException(IResponseCode code, String message, Throwable cause, Map msgParams) {
        super(message, cause);
        this.code = code;
        this.msgParams = msgParams;
    }

    public WfBaseException(IResponseCode code, String message, Map msgParams) {
        super(message);
        this.code = code;
        this.msgParams = msgParams;
    }

    public WfBaseException(IResponseCode code, Map msgParams) {
        super(ExceptionUtil.renderString(code.logMsg(), msgParams));
        this.code = code;
        this.msgParams = msgParams;
    }

    public WfBaseException(IResponseCode code) {
        super(code.logMsg());
        this.code = code;
    }

    public WfBaseException(int code, String friendlyMsg, String logMsg) {
        super(logMsg);
        this.code = new IResponseCode() {
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
        };
    }


    public IResponseCode getCode() {
        return code;
    }

    public void setCode(IResponseCode code) {
        this.code = code;
    }

    public Map getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(Map msgParams) {
        this.msgParams = msgParams;
    }
}
