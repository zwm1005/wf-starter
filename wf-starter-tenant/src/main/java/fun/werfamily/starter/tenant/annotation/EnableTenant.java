package fun.werfamily.starter.tenant.annotation;


import fun.werfamily.starter.tenant.FeignClientInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Mr.WenMing
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Import(value = FeignClientInterceptor.class)
public @interface EnableTenant {

}
