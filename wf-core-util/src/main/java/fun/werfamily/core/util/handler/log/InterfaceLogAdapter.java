package fun.werfamily.core.util.handler.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fun.werfamily.base.exception.WfBaseException;
import fun.werfamily.base.response.IResponseCode;
import fun.werfamily.base.response.Result;
import fun.werfamily.base.response.SysResponseCode;
import fun.werfamily.base.util.ExceptionUtil;
import fun.werfamily.core.util.enums.WfLogLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;

import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/9.
 */
@Component
@Slf4j
public class InterfaceLogAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceLogAdapter.class);

    public static final String TRACE_ID = "traceId";

    /**
     * 不输出日志配置规则（采用关键字匹配出现则跳过日志输出）
     */
    @Value("${wf.aoplog.method.filter:nothing_value}")
    public String aoplogNoLog;


    /**
     * 用于 未补货异常处理机制
     */
    public interface ThrowableHandler {
        /**
         * 接口异常处理
         *
         * @param throwable
         * @return 最终返回结果
         */
        Object handler(Throwable throwable);

    }

    /**
     * AOP 接口日志输出重载
     *
     * @param logName 日志模块名称
     * @param jp      ProceedingJoinPoint Spring AOP @Around 环绕通知接口参数
     */
    public Object doAopMethodLog(String logName, ProceedingJoinPoint jp, ThrowableHandler throwableHandler) {
        Object rvt = null;
        try {
            rvt = jp.proceed(jp.getArgs());
        } catch (Throwable throwable) {
            rvt = throwableHandler.handler(throwable);
            return rvt;
        }
//        aopMethodLog(logName, jp, rvt);
        return rvt;
    }

    /**
     * AOP 接口日志输出 但不处理异常、错误
     *
     * @param logName 日志模块名称
     * @param jp      ProceedingJoinPoint Spring AOP @Around 环绕通知接口参数
     */
    public Object doAopMethodLogThrow(String logName, ProceedingJoinPoint jp) throws Throwable {
        Object rvt = null;
        try {
            rvt = jp.proceed(jp.getArgs());
        } catch (Throwable throwable) {
            aopMethodLog(logName, WfLogLevel.ERROR, jp, throwable, null);
            throw throwable;
        }
        aopMethodLog(logName, jp, rvt, WfLogLevel.ERROR);
        return rvt;
    }


    /**
     * AOP 接口日志输出重载
     *
     * @param logName 日志模块名称
     * @param jp      ProceedingJoinPoint Spring AOP @Around 环绕通知接口参数
     */
    public Object doAopMethodLog(String logName, ProceedingJoinPoint jp) {
        return doAopMethodLog(logName, jp, new ThrowableHandler() {
            @Override
            public Object handler(Throwable throwable) {
                Result result = null;
                String traceId = MDC.get(TRACE_ID);

                if (throwable instanceof WfBaseException) {
                    WfBaseException e = (WfBaseException) throwable;
                    IResponseCode responseCode = e.getCode();
                    result = Result.error(responseCode, traceId);
                } else if (throwable instanceof ValidationException) {
                    ValidationException e = (ValidationException) throwable;
                    result = Result.error(SysResponseCode.ERROR_PARAM.code(), e.getMessage(), traceId);
                } else {
                    result = Result.error(SysResponseCode.SERVER_ERROR, traceId);
                }

                result.setTraceId(traceId);
                aopMethodLog(logName, WfLogLevel.ERROR, jp, throwable, traceId);
                return result;
            }
        });
    }

    /**
     * AOP 接口日志输出重载
     *
     * @param logName     日志模块名称
     * @param jp          ProceedingJoinPoint Spring AOP @Around 环绕通知接口参数
     * @param returnvalue 接口返回值
     */
    public void aopMethodLog(String logName, ProceedingJoinPoint jp, Object returnvalue, WfLogLevel wfLogLevel) {
        try {
            Object[] argsObj = jp.getArgs();
            StringBuffer argsBuff = new StringBuffer();
            String args = "";
            for (Object o : argsObj) {
                if (o instanceof BindingResult) {
                    continue;
                }

                if (!ObjectUtils.isEmpty(argsBuff.toString())) {
                    argsBuff.append(",");
                }
                argsBuff.append(JSONObject.toJSONString(o));
            }
            args = argsBuff.toString();

            String className = jp.getTarget().getClass().getName();
            String methodName = jp.getSignature().getName();

            aopMethodLog(logName, wfLogLevel, className, methodName, args, JSONObject.toJSONString(returnvalue), jp.getTarget().getClass(), null);
        } catch (Exception e) {
            LOGGER.error("输出日志异常", e);
        }

    }


    /**
     * AOP 接口日志输出重载
     *
     * @param logName     日志模块名称
     * @param logLevel    日志级别
     * @param jp          ProceedingJoinPoint Spring AOP @Around 环绕通知接口参数
     * @param returnValue 接口返回值
     */
    public void aopMethodLog(String logName, WfLogLevel logLevel, ProceedingJoinPoint jp, Object returnValue, String traceId) {
        if (jp != null) {
            String args = JSONObject.toJSONString(jp.getArgs());
            String className = jp.getTarget().getClass().getName();
            String methodName = jp.getSignature().getName();
            aopMethodLog(logName, logLevel, className, methodName, args, returnValue, jp.getTarget().getClass(), traceId);
        } else {
            if (returnValue instanceof Throwable) {
                logName += ((Throwable) returnValue).getMessage();
            }
            log(LoggerFactory.getLogger(this.getClass()), logLevel, logName, returnValue);
        }

    }

    /**
     * AOP 接口日志输出
     *
     * @param logName     日志模块名称
     * @param logLevel    日志级别
     * @param className   接口所属类
     * @param methodName  接口方法
     * @param args        接口参数
     * @param returnValue 接口返回值
     */
    private void aopMethodLog(String logName
            , WfLogLevel logLevel
            , String className
            , String methodName
            , String args
            , Object returnValue
            , Class c
            , String traceId) {

        Map<String, String> errorResponseCode = new HashMap<>(3);

        if (returnValue instanceof WfBaseException) {
            WfBaseException wfBaseException = (WfBaseException) returnValue;
            String logMsg = ExceptionUtil.renderString(wfBaseException.getCode().logMsg(), wfBaseException.getMsgParams());
            errorResponseCode.put("code", wfBaseException.getCode().code() + "");
            errorResponseCode.put("friendlyMsg", wfBaseException.getCode().friendlyMsg() + "");
            errorResponseCode.put("logMsg", logMsg);
        }

        String param = MessageFormat.format(logLevel.messageModel
                , logName
                , className
                , methodName
                , args
                , errorResponseCode.isEmpty() ? returnValue : JSON.toJSONString(errorResponseCode)
                , traceId);

        //接口未捕获的异常 或者error
        if (returnValue instanceof Throwable) {
            log(LoggerFactory.getLogger(c), logLevel, param, returnValue);
            return;
        }

        //不做日志处理的接口
        String[] noLogs = aoplogNoLog.split(",");

        for (String nolog : noLogs) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(nolog)
                    && methodName.toLowerCase().indexOf(nolog) != -1
                    && !logLevel.value.equals(WfLogLevel.ERROR.value)) {
                return;
            }
        }

        try {
            MethodUtils.invokeExactMethod(LoggerFactory.getLogger(c), logLevel.value, param);
        } catch (Exception e) {
            LOGGER.error(" AOP 接口日志添加出错！ ", e);
        }
    }

    public void log(Logger logger, WfLogLevel logLevel, String logName, Object... params) {
        try {
            MethodUtils.invokeExactMethod(logger, logLevel.value, logName, params);
        } catch (Exception e) {
            LOGGER.error(" AOP 接口日志添加出错！ ", e);
        }
    }

}
