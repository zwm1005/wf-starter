package fun.werfamily.starter.cache.annotaion;



import java.lang.annotation.*;

/**
 * Description: 自定义缓存注解
 *
 * @Author: Mr.WenMing
 * Created on 2021/12/10.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WfCache {
    /**
     * 缓存key前缀
     *
     * @return
     */
    String value();

    /**
     * 依赖的缓存Key
     * AOP 清除缓存用
     * @return
     */
    String[] relationKeys() default {};

    Class clazz();

    boolean isList() default false;

    String[] values() default {};  //AOP 清除缓存用 依赖缓存KEY

    /**
     * 缓存类型  Redis、Hash-Redis、Local
     * @return
     */
    WfCacheTypeEnum type() default WfCacheTypeEnum.REDIS;

    /**
     * 默认缓存3天
     * @return
     */
    long timeOut() default 3*60*60*24;


    /**
     * 如果查询条件为对象类型，可以设置使用某些field作为key
     * @return
     */
    String[] priorityKeys() default {};

}
