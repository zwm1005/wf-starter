package fun.werfamily.framework.log.audit.annotaion;

import java.lang.annotation.*;

/**
 * @Author Mr.WenMing
 * @date 2023/12/1 15:05
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessId {

}
