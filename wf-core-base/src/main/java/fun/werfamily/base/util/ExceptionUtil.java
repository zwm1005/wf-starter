package fun.werfamily.base.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2021/12/9.
 */

public class ExceptionUtil {

    private static final Logger log = LoggerFactory.getLogger(ExceptionUtil.class);

    /**
     * 占位符替换
     *
     * @param content 日志内容
     * @param map     自定义参数
     * @return 格式化内容
     */
    public static String renderString(String content, Map<String, String> map) {
        try {
            if (map == null || map.size() == 0) {
                String regex1 = "\\$\\{(.*?)}";
                Pattern pattern1 = Pattern.compile(regex1);
                content = RegExUtils.removeAll(content, pattern1);
                return content;
            }

            content = StringSubstitutor.replace(content, map);
        } catch (Exception e) {
            log.warn("参数解析异常 content = {} map = {}", content, JSON.toJSONString(map), e);
        }

        return content;
    }

}
