package fun.werfamily.starter.sentinel.cloud.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import fun.werfamily.base.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2022/2/22 15:40
 */
public class SentinelExceptionHandler implements BlockExceptionHandler {

    @Autowired
    private SentinelConfiguration sentinelConfiguration;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        // Return 429 (Too Many Requests)
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        // flush output stream
        PrintWriter out = response.getWriter();
        out.print(JSON.toJSONString(Result.error(HttpStatus.TOO_MANY_REQUESTS.value(), sentinelConfiguration.getBlockedMessage(), null)));
        out.flush();
        out.close();
    }

    public SentinelExceptionHandler() {

    }

}
