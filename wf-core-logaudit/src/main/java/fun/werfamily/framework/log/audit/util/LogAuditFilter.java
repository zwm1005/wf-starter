package fun.werfamily.framework.log.audit.util;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LogAuditFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (WebUtils.isUploadRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper((HttpServletRequest) request);
        }

        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {

    }

    public static class RequestWrapper extends HttpServletRequestWrapper {

        /**
         * 请求体
         */
        private String body;

        private Map<String, String[]> parameterMap;

        public RequestWrapper(HttpServletRequest request) {
            super(request);
            // 解决post form表单，通过getParameter获取不到值的问题
            parameterMap = request.getParameterMap();
            // 将body数据存储起来
            body = getBody(request);
        }

        /**
         * 获取请求体
         *
         * @param request 请求
         * @return 请求体
         */
        private String getBody(HttpServletRequest request) {
            return WebUtils.getBody(request);
        }

        /**
         * 获取请求体
         *
         * @return 请求体
         */
        public String getBody() {
            return body;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            // 创建字节数组输入流
            final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }

                @Override
                public int read() throws IOException {
                    return bais.read();
                }
            };
        }
    }
}
