package fun.werfamily.base.exception;

import fun.werfamily.base.response.IResponseCode;

import java.util.Map;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2021/12/9.
 */
@SuppressWarnings({"rawtypes"})
public class WfServiceException extends WfBaseException {

    public WfServiceException(IResponseCode code, String message, Throwable cause, Map msgParams) {
        super(code, message, cause, msgParams);
    }

    public WfServiceException(IResponseCode code, String message, Map msgParams) {
        super(code, message, msgParams);
    }

    public WfServiceException(IResponseCode code, Map msgParams) {
        super(code, msgParams);
    }

    public WfServiceException(IResponseCode code) {
        super(code);
    }

    public WfServiceException(int code, String friendlyMsg, String logMsg) {
        super(code, friendlyMsg, logMsg);
    }

}
