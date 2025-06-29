package fun.werfamily.core.util.handler.log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import fun.werfamily.base.exception.WfBaseException;
import fun.werfamily.base.exception.WfValidationException;
import fun.werfamily.base.response.Result;
import fun.werfamily.base.response.SysResponseCode;
import fun.werfamily.core.util.enums.WfLogLevel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ValidationException;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/10.
 */
@Slf4j
public abstract class BaseAopLogHandler<T extends Result<?>> implements AopLogHandler<T> {

    @Autowired
    protected InterfaceLogAdapter interfaceLogAdapter;

    @Override
    public T throwableHandle(Throwable throwable, ProceedingJoinPoint jp, String logName, String alarmKeywords) {
        T result;
        String traceId = MDC.get("traceId");

        if (throwable instanceof WfBaseException) {
            WfBaseException e = (WfBaseException) throwable;
            result = buildWfBaseExceptionResult(e, traceId);
        } else if (throwable instanceof ValidationException) {
            ValidationException e = (ValidationException) throwable;
            result = buildValidationExceptionResult(e, traceId);
        } else if (throwable instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) throwable;
            result = buildMethodArgumentNotValidException(e, traceId);
        } else if (throwable instanceof InvalidFormatException) {
            InvalidFormatException e = (InvalidFormatException) throwable;
            result = buildInvalidFormatExceptionResult(e, traceId);
        } else if (throwable instanceof JsonParseException) {
            JsonParseException e = (JsonParseException) throwable;
            result = buildJsonParseExceptionResult(e, traceId);
        } else if (throwable instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException e = (MissingServletRequestParameterException) throwable;
            result = buildMissingServletRequestParameterExceptionResult(e, traceId);
        } else if (throwable instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException e = (HttpMessageNotReadableException) throwable;
            result = buildHttpMessageNotReadableExceptionResult(e, traceId);
        } else {
            logName += "["+alarmKeywords+"]" + logName;
            result = buildExceptionResult(SysResponseCode.SERVER_ERROR, traceId);
        }

        //如果是指定验证异常或者是框架参数验证异常则不打印ERROR日志
        boolean validateFailed = throwable instanceof WfValidationException ||
                throwable instanceof ValidationException ||
                throwable instanceof MethodArgumentNotValidException ||
                throwable instanceof InvalidFormatException ||
                throwable instanceof JsonParseException ||
                throwable instanceof MissingServletRequestParameterException ||
                throwable instanceof HttpMessageNotReadableException;
        if (validateFailed) {
            interfaceLogAdapter.aopMethodLog(logName, WfLogLevel.WARN, jp, throwable, traceId);
        } else {
            interfaceLogAdapter.aopMethodLog(logName, WfLogLevel.ERROR, jp, throwable, traceId);
        }

        return result;
    }

    /**
     * spring 参数校验失败
     *
     * @param throwable
     * @param traceId
     * @return
     */
    abstract T buildMethodArgumentNotValidException(MethodArgumentNotValidException throwable, String traceId);

    /**
     * WfBaseException异常时构建返回值
     *
     * @param e
     * @param traceId
     * @return
     */
    abstract T buildWfBaseExceptionResult(WfBaseException e, String traceId);

    /**
     * ValidationException 异常时构建返回值
     *
     * @param e
     * @param traceId
     * @return
     */
    abstract T buildValidationExceptionResult(ValidationException e, String traceId);

    /**
     * InvalidFormatException 异常时构建返回值
     *
     * @param e
     * @param traceId
     * @return
     */
    abstract T buildInvalidFormatExceptionResult(InvalidFormatException e, String traceId);

    /**
     * JsonParseException 异常时构建返回值
     *
     * @param e
     * @param traceId
     * @return
     */
    abstract T buildJsonParseExceptionResult(JsonParseException e, String traceId);

    /**
     * MissingServletRequestParameterException 异常时构建返回值
     *
     * @param e
     * @param traceId
     * @return
     */
    abstract T buildMissingServletRequestParameterExceptionResult(MissingServletRequestParameterException e, String traceId);

    /**
     * HttpMessageNotReadableException 异常时构建返回值
     *
     * @param e
     * @param traceId
     * @return
     */
    abstract T buildHttpMessageNotReadableExceptionResult(HttpMessageNotReadableException e, String traceId);


    /**
     * 非自定义异常时构建返回值
     *
     * @param sysResponseCode
     * @param traceId
     * @return
     */
    abstract T buildExceptionResult(SysResponseCode sysResponseCode, String traceId);

}
