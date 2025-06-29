package fun.werfamily.core.util.handler.log;

import fun.werfamily.core.util.annotation.WfLog;
import fun.werfamily.core.util.enums.WfLogLevel;
import fun.werfamily.core.util.enums.WfLogType;
import fun.werfamily.core.util.spring.SpringContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/9.
 */
@Aspect
@Component
public class WfLogAopStarter implements Ordered {

    @Autowired
    private InterfaceLogAdapter interfaceLogAdapter;

    @Value("${wf.starter.log.openWfLog:true}")
    private boolean openWfLog;

    @Value("${wf.starter.log.aopLogHandler:DefaultAopLogHandler}")
    private String aopLogHandlerBeanName;

    /**
     * @param jp
     * @return
     */
    @Around("@annotation(fun.werfamily.core.util.annotation.WfLog)")
    public Object wfCacheLoadAddHandlerAdapter(ProceedingJoinPoint jp) throws Throwable {
        if (!openWfLog) {
            return jp.proceed(jp.getArgs());
        }

        AopLogHandler aopLogHandler = SpringContextUtil.getBean(aopLogHandlerBeanName);

        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        WfLog wfLog = methodSignature.getMethod().getAnnotation(WfLog.class);
        String logName = wfLog.value();
        WfLogLevel wfLogLevel = wfLog.level();
        WfLogType wfLogType = wfLog.type();

        Object rvt = null;


        //只有CONTROLLER 层才捕捉异常
        try {
            rvt = jp.proceed(jp.getArgs());
            afterProceedSuccess(logName, jp, rvt, wfLogLevel);
        } catch (Throwable throwable) {
            if (wfLogType == WfLogType.CONTROLLER) {
                return aopLogHandler.throwableHandle(throwable, jp, logName, wfLog.alarmKeywords());
            } else {
                throw throwable;
            }
        }

        return rvt;
    }

    private void afterProceedSuccess(String logName, ProceedingJoinPoint jp, Object rvt, WfLogLevel wfLogLevel) {
        if (wfLogLevel != WfLogLevel.ERROR) {
            interfaceLogAdapter.aopMethodLog(logName, jp, rvt, wfLogLevel);
        }
    }


    @Override
    public int getOrder() {
        return 1001;
    }

}
