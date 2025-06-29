package fun.werfamily.core.util.handler.log;

import fun.werfamily.core.util.spring.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/12.
 */
@RestController
@Slf4j
public class ControllerExceptionHandleAdvice {

    @Value("${wf.starter.log.aopLogHandler:DefaultAopLogHandler}")
    private String aopLogHandlerBeanName;

    @ExceptionHandler
    public Object handler(HttpServletRequest req, HttpServletResponse res, Exception e) {
        AopLogHandler<?> aopLogHandler = SpringContextUtil.getBean(aopLogHandlerBeanName);
        return aopLogHandler.throwableHandle(e, null, "", "");
    }


}
