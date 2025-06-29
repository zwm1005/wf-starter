package fun.werfamily.starter.mq.rocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: RocketMQ的配置参数
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/1.
 */
@SuppressWarnings("ALL")
@Data
@ConfigurationProperties(prefix = "spring.rocketmq")
public class MQProperties {
    /**
     * config name server address
     */
    private String nameServerAddress;
    /**
     * config producer group , default to DPG+RANDOM UUID like DPG-fads-3143-123d-1111
     */
    private String producerGroup;
    /**
     * config send message timeout
     */
    private Integer sendMsgTimeout = 3000;
    /**
     * switch of trace message consumer: send message consumer info to topic: rmq_sys_TRACE_DATA
     */
    private Boolean traceEnabled = Boolean.TRUE;

    /**
     * switch of send message with vip channel
     */
    private Boolean vipChannelEnabled = Boolean.TRUE;

}
