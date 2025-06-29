package fun.werfamily.framework.log.audit.provider;

import fun.werfamily.framework.log.audit.annotaion.AuditIgnore;
import fun.werfamily.framework.log.audit.annotaion.AuditIgnoreChanges;
import fun.werfamily.framework.log.audit.annotaion.BusinessFieldDesc;
import fun.werfamily.framework.log.audit.annotaion.BusinessId;
import fun.werfamily.framework.log.audit.util.ContextUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author Mr.WenMing
 * @date 2023/12/4 16:30
 */
@Component
@Slf4j
public class BaseLogChangesDescProvider<T> implements ILogChangesDescProvider {

    @Override
    public String providerChangesDesc(Object oldParams, Object params) {
        if (null == params) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        deepCompareObj(params, oldParams, stringBuilder, oldParams == null);
        return stringBuilder.toString();
    }

    public static void deepCompareObj(Object params, Object oldParams, StringBuilder stringBuilder, boolean isAdd) {
        if (null == params) {
            return;
        }
        if (null == stringBuilder) {
            stringBuilder = new StringBuilder();
        }
        Field[] fields = params.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field != null) {
                // 跳过
                int modifiers = field.getModifiers();
                if (field.isAnnotationPresent(AuditIgnore.class) || field.isAnnotationPresent(BusinessId.class)
                        || Modifier.isStatic(modifiers)
                        || Modifier.isFinal(modifiers)) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Object o11 = PropertyUtils.getProperty(params, field.getName());
                    Object o12 = null != oldParams ? PropertyUtils.getProperty(oldParams, field.getName()) : null;
                    compareObj(field, o11, o12, stringBuilder, isAdd);
                } catch (Exception e) {
                    log.error("比较变更内容异常,params={}, OldParams={}", params, oldParams, e);
                }
            }
        }
    }


    public static void compareObj(Field field, Object o1, Object o2, StringBuilder stringBuilder, boolean isAdd) {
        if (o1 == null && o2 == null) {
            return;
        }
        Class clazz = o1 == null ? o2.getClass() : o1.getClass();

        if (clazz.getName().indexOf("java.lang.") == 0
                || clazz.getName().indexOf("java.math.") == 0
                || clazz.getName().indexOf("java.time") == 0
                || clazz.getName().indexOf("java.util.Date") == 0) {
            boolean isEquals;
            if ("java.math.BigDecimal".equals(clazz.getName()) && o1 != null && o2 != null) {
                isEquals = ((BigDecimal) o1).compareTo((BigDecimal) o2) == 0;
            }else {
                isEquals = Objects.equals(o1, o2);
            }
            if (!isEquals) {
                // 字段描述默认为fieldName 有注解的取ApiModelProperty的value
                String fieldDesc = field.getName();
                if (field.isAnnotationPresent(ApiModelProperty.class)) {
                    ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                    fieldDesc = apiModelProperty.value();
                }
                try {
                    boolean isIgnoreChanges = field.isAnnotationPresent(AuditIgnoreChanges.class);
                    // 获取字段枚举值的描述
                    if (field.isAnnotationPresent(BusinessFieldDesc.class)) {
                        BusinessFieldDesc businessFieldDesc = field.getAnnotation(BusinessFieldDesc.class);
                        Object bean = ContextUtil.getBean(businessFieldDesc.className());
                        Class bizClass = bean.getClass();
                        Method valueMethod = bizClass.getDeclaredMethod(businessFieldDesc.methodName(), clazz);
                        valueMethod.setAccessible(true);
                        Object fieldNewValue = o1 == null ? null : valueMethod.invoke(bean, o1);
                        Object fieldOldValue = o2 == null ? null : valueMethod.invoke(bean, o2);
                        String fieldChangeDesc = getFieldChangeDesc(fieldDesc, fieldNewValue, fieldOldValue, isAdd, isIgnoreChanges);
                        stringBuilder.append(fieldChangeDesc);
                    } else {
                        String fieldChangeDesc = getFieldChangeDesc(fieldDesc, o1, o2, isAdd, isIgnoreChanges);
                        stringBuilder.append(fieldChangeDesc);
                    }
                } catch (Exception e) {
                    log.warn("拼接日志变更描述异常field={},o1{},o2{}", field, o1, o2, e);
                }
            }
            return;
        } else if (clazz.getName().indexOf("java.util.") == 0) {
            // 集合暂时只做toString处理
            if (!Objects.equals(o1, o2)) {
                // 字段描述默认为fieldName 有注解的取ApiModelProperty的value
                String fieldDesc = field.getName();
                if (field.isAnnotationPresent(ApiModelProperty.class)) {
                    ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                    fieldDesc = apiModelProperty.value();
                }
                boolean isIgnoreChanges = field.isAnnotationPresent(AuditIgnoreChanges.class);
                String o1ValueStr = o1 == null ? null : o1.toString();
                String o2ValueStr = o2 == null ? null : o2.toString();
                String fieldChangeDesc = getFieldChangeDesc(fieldDesc, o1ValueStr, o2ValueStr, isAdd, isIgnoreChanges);
                stringBuilder.append(fieldChangeDesc);
            }
            return;
        }
        deepCompareObj(o1, o2, stringBuilder, isAdd);
    }

    /**
     * 获取属性变更描述
     *
     * @param fieldDesc
     * @param fieldOldValue
     * @param fieldNewValue
     * @return
     */
    private static String getFieldChangeDesc(String fieldDesc, Object fieldNewValue, Object fieldOldValue, boolean isAdd, boolean ignoreChanges) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isEmpty(fieldOldValue) && isEmpty(fieldNewValue)) {
            return "";
        }
        if (isAdd || ignoreChanges) {
            stringBuilder.append(fieldDesc);
            stringBuilder.append(": ");
            stringBuilder.append(fieldNewValue);
            stringBuilder.append("\r");
        } else {
            stringBuilder.append(fieldDesc);
            stringBuilder.append("由");
            stringBuilder.append("【");
            stringBuilder.append(isEmpty(fieldOldValue) ? "" : String.valueOf(fieldOldValue));
            stringBuilder.append("】");
            stringBuilder.append("修改为");
            stringBuilder.append("【");
            stringBuilder.append(isEmpty(fieldNewValue) ? "" : String.valueOf(fieldNewValue));
            stringBuilder.append("】");
            stringBuilder.append("\r");
        }
        return stringBuilder.toString();
    }

    static boolean isEmpty(Object fieldOldValue) {
        return StringUtils.isEmpty(String.valueOf(fieldOldValue)) || "null".equals(String.valueOf(fieldOldValue));
    }
}
