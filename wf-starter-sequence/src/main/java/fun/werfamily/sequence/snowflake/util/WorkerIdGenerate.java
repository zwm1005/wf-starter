package fun.werfamily.sequence.snowflake.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RKeys;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


/**
 * @Author luofuchuan
 */
@Slf4j
@Component
public class WorkerIdGenerate {

    @Autowired
    private RedissonClient redissonClient;
    /**
     * lua 脚本
     */
    protected static final String script =
            "local now = redis.call('TIME')[1]\n" +
                    "local idWordsKey = KEYS[1]\n" +
                    "local ServiceName = KEYS[2]\n" +
                    "local sp = ':'\n" +
                    "for i = 0, 15 do\n" +
                    "    local ServiceKey = idWordsKey..sp..ServiceName..sp..i\n" +
                    "    if redis.call('SETNX', ServiceKey, now) == 1 then\n" +
                    "        redis.call('Expire', ServiceKey, 60)\n" +
                    "        return i;\n" +
                    "    end\n" +
                    "end\n" +
                    "return -1";


    private String redisKey;

    private WorkerIdGenerate() {
    }

    /**
     * workId机器生成 0-15
     *
     * @param redisson
     * @return
     */
    public long getWorkerId(String bizCode) {
        List<Object> keys = Stream.of("WORKID", bizCode).collect(toList());
        List<String> stringKeys = Stream.of("WORKID", bizCode).collect(toList());
        // 实例化脚本对象
        RScript rScript = redissonClient.getScript();
        // 获取序列号
        Long eval = (Long) rScript.eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.INTEGER, keys,stringKeys);
        String key = String.join(":", stringKeys) + ":" + eval;
        // -1 代表机器用完了，重试
        if (eval < 0) {
            log.error("目前机器Id已用完，请调整实例");
            return eval;
        }
        redisKey = key;
        autoExpire(key);
        return eval;
    }


    private void autoExpire(String key) {
        RKeys keys = redissonClient.getKeys();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("id_auto_expire-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(() -> {
            keys.expire(key, 60, TimeUnit.SECONDS);
            log.debug("自动续期id成功:{}", key);
        }, 0, 30, TimeUnit.SECONDS);
    }

    public void destroyWorkId() {
        RKeys keys = redissonClient.getKeys();
        log.debug("退订id成功:{}", redisKey);
        keys.delete(redisKey);
    }
}
