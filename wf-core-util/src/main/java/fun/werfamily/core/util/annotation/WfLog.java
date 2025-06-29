package fun.werfamily.core.util.annotation;


import fun.werfamily.core.util.enums.WfLogLevel;
import fun.werfamily.core.util.enums.WfLogType;

import java.lang.annotation.*;

/**
 * Description: 日志注解
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/9.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WfLog {

    /**
     * 补充日志内容
     *
     * @return
     */
    String value() default "";

    /**
     * 默认日志级别
     *
     * @return
     */
    WfLogLevel level() default WfLogLevel.ERROR;

    /**
     * 默认日志级别类型
     *
     * @return
     */
    WfLogType type() default WfLogType.CONTROLLER;

    /**
     * 运行时异常&非自定义异常追加重点告警关键字（核心业务异常）
     * @return
     */
    String alarmKeywords() default "";


}
