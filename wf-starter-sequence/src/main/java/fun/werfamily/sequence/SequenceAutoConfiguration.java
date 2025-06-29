package fun.werfamily.sequence;

import fun.werfamily.sequence.snowflake.service.impl.CachedUidGenerator;
import fun.werfamily.sequence.snowflake.service.impl.SnowflakeIdWorker;
import fun.werfamily.sequence.snowflake.service.impl.UniqueIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ID生成器配置
 *
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@Configuration
@ConfigurationProperties(prefix = "fun.werfamily.sequence")
@ConditionalOnClass({SpringBootApplication.class, RedissonClient.class})
public class SequenceAutoConfiguration {

    /**
     * 业务线编码 一般为应用名
     * 此标识用来做 machineId 存储 必填
     */
    private String bizCode;

    @Bean
    public CachedUidGenerator cachedUidGenerator() {
        return new CachedUidGenerator();
    }

    @Bean
    public UniqueIdGenerator uniqueIdGenerator() {
        return new UniqueIdGenerator();
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker();
    }


    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }
}
