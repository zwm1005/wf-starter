package fun.werfamily.framework.log.audit.annotaion;

import java.lang.annotation.*;

/**
 * @Author Mr.WenMing
 * @date 2023/12/5 13:35
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessFieldDesc {
    /**
     * 被此注解注释的字段 将会从提供的枚举类中 根据该字段的值获取枚举实例的描述
     * @return
     */
    String className();

    /**
     * 根据值获取枚举实例的方法
     * @return
     */
    String methodName();
}
