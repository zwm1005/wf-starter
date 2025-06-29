package fun.werfamily.starter.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: Mr.WenMing
 * @since: 2021/5/13
 */
@ConfigurationProperties(prefix = "feign.circuitbreaker")
public class CircuitBreakerProperties {

    private TimeLimiter timeLimiter = new TimeLimiter();

    public TimeLimiter getTimeLimiter() {
        return timeLimiter;
    }

    public void setTimeLimiter(TimeLimiter timeLimiter) {
        this.timeLimiter = timeLimiter;
    }

    static class TimeLimiter {

        /**
         * 请求超时设置
         */
        private Long timeOutMills = 5000L;

        public Long getTimeOutMills() {
            return timeOutMills;
        }

        public void setTimeOutMills(Long timeOutMills) {
            this.timeOutMills = timeOutMills;
        }
    }
}