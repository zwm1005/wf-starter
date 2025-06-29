package fun.werfamily.framework.log.audit.util;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public final class WebUtils {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String CONTENT_TYPE = "multipart/form-data";

    private WebUtils() {
    }

    /**
     * 获取ip
     *
     * @param request
     * @return
     */
    public static String getIP(HttpServletRequest request) {
        String ip = null;

        ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * 获取请求参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParameters(HttpServletRequest request) {


        Enumeration<String> names = request.getParameterNames();

        Map<String, String> params = new HashMap<>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getParameter(name);
            params.put(name, value);
        }

        return params;
    }

    /**
     * 获取请求body
     *
     * @param request
     * @return
     */
    public static String getBody(HttpServletRequest request) {
        ServletRequest requestWrapper = request;
        while (requestWrapper instanceof HttpServletRequestWrapper) {
            if (requestWrapper instanceof LogAuditFilter.RequestWrapper) {
                return ((LogAuditFilter.RequestWrapper) requestWrapper).getBody();
            }

            requestWrapper = ((HttpServletRequestWrapper) requestWrapper).getRequest();
        }

        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(LINE_SEPARATOR);
            }
        } catch (IOException e) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String body = sb.toString();
        if (null != body && body.length() > 0 && body.endsWith(LINE_SEPARATOR)) {
            body = body.substring(0, body.length() - LINE_SEPARATOR.length());
        }

        return body;
    }

    public static boolean isUploadRequest(ServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().indexOf(CONTENT_TYPE) > -1;
    }
}
