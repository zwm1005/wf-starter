package fun.werfamily.starter.cache.annotaion;

import java.lang.annotation.*;

/**
 * Description:
 * @Author: Mr.WenMing
 * Created on 2021/12/14.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WfCacheDel {

    /**
     * 缓存key前缀
     *
     * @return
     */
    String value() default "";

    /**
     * 如果查询条件为对象类型，可以设置使用某些field作为key
     * @return
     */
    String[] priorityKeys() default {};

    /**
     * 缓存类型  Redis、Hash-Redis、Local
     * @return
     */
    WfCacheTypeEnum type() default WfCacheTypeEnum.REDIS;

}
