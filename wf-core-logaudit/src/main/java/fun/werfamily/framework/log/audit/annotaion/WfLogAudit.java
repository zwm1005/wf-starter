package fun.werfamily.framework.log.audit.annotaion;

import fun.werfamily.framework.log.audit.model.LogGroupEnum;
import fun.werfamily.framework.log.audit.provider.BaseLogChangesDescProvider;
import fun.werfamily.framework.log.audit.provider.BaseLogExtendExtendInfoProvider;

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
public @interface WfLogAudit {

    /**
     * api
     */
    String value();

    /**
     * api描述
     */
    String desc();

    /**
     * 日志分组
     */
    LogGroupEnum group() default LogGroupEnum.DEFAULT;

    /**
     * bean操作者解析器
     */
    String providerBean() default "baseOperatorProvider";

    /**
     * 日志扩展信息解析器
     * @return
     */
    Class extendInfoProvider() default BaseLogExtendExtendInfoProvider.class;

    /**
     * 操作变更内容解析器
     * @return
     */
    Class changesDescProvider() default BaseLogChangesDescProvider.class;
}
