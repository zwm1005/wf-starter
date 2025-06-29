package fun.werfamily.framework.log.audit.annotaion;

import java.lang.annotation.*;

/**
 * @Author Mr.WenMing
 * @date 2023/12/6 15:29
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditIgnoreChanges {
}
