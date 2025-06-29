package fun.werfamily.core.util.handler.log;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/9.
 */
public interface AopLogHandler<T> {

    /**
     * 异常处理器
     *
     * @param throwable 异常
     * @param jp        切面
     * @param logName   日志名
     * @return
     */
    T throwableHandle(Throwable throwable, ProceedingJoinPoint jp, String logName, String alarmKeywords);

}
