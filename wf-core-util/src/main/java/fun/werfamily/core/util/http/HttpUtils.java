package fun.werfamily.core.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * 获取HTTP通用的请求头信息，如租户信息
 *
 * @AuthorMr.WenMing
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final String TENANT_ID = "tenantId";

    /**
     * 优先从header获取，其次从requestString获取
     *
     * @param key 键
     * @return 值
     */
    public static String getHeadValue(String key) {
        String tenantId = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                tenantId = request.getHeader(key);
                if (tenantId == null || tenantId.trim().length() == 0) {
                    Object o = request.getAttribute(key);
                    if (o != null) {
                        tenantId = o.toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getHeadValue of tenantId failed.", e);
            return null;
        }
        return tenantId;
    }

    public static void setObject(String key, Object val) {
        HttpServletRequest request = ((ServletRequestAttributes)
                (RequestContextHolder.currentRequestAttributes())).getRequest();
        request.setAttribute(key, val);
    }

    public static String getTenantId() {
        return getHeadValue(TENANT_ID);
    }

    public static void setTenantId(Object value) {
        setObject(TENANT_ID, value);
    }
}
