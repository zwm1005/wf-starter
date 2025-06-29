//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package fun.werfamily.base.response;

import java.io.Serializable;

/**
 * @AuthorMr.WenMing
 */
@SuppressWarnings("unused")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -8782333365744933352L;
    protected int status;
    protected String message;
    protected boolean success = true;
    protected T data;
    protected String traceId;

    protected Result() {
    }

    protected Result(int status, String message) {
        this.status = status;
        this.message = message;
    }

    protected Result(int status, T data, String message) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> error(String traceId) {
        return error(SysResponseCode.SERVER_ERROR.code(), SysResponseCode.SERVER_ERROR.friendlyMsg(), traceId);
    }

    public static <T> Result<T> error(String message, String traceId) {
        return error(SysResponseCode.SERVER_ERROR.code(), message, traceId);
    }

    public static <T> Result<T> error(int status, String message, String traceId) {
        Result<T> result = new Result<>(status, message);
        result.setSuccess(false);
        result.setTraceId(traceId);
        return result;
    }

    public static <T> Result<T> error(int status, T data, String message, String traceId) {
        Result<T> result = new Result<>(status, data, message);
        result.setSuccess(false);
        result.setTraceId(traceId);
        return result;
    }

    public static <T> Result<T> error(IResponseCode msg, String traceId) {
        return error(msg.code(), msg.friendlyMsg(), traceId);
    }

    public static <T> Result<T> success() {
        return success(null, SysResponseCode.OK.friendlyMsg());
    }

    public static <T> Result<T> success(T data) {
        return success(data, SysResponseCode.OK.friendlyMsg());
    }

    public static <T> Result<T> success(String message) {
        return success(null, message);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(200, data, message);
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result<T> data(T data) {
        return success(data, (String) null);
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
