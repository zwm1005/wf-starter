//package fun.werfamily.starter.cache.manager;
//
//import fun.werfamily.starter.cache.config.RedisCacheConfig;
//import org.redisson.api.RedissonClient;
//import org.redisson.spring.cache.RedissonSpringCacheManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.SerializationException;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.io.IOException;
//import java.util.Objects;
//
///**
// * Description: 缓存管理器
// *
// * @Author : Mr.WenMing
// * @create 2022/2/24 19:32
// */
//@SuppressWarnings("unchecked")
//@Configuration
//@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
//@EnableCaching
//@ImportAutoConfiguration(classes = RedisCacheConfig.class)
//public class RedisCacheManager {
//
//    @Autowired(required = false)
//    private RedisCacheConfig redisCacheConfig;
//
//    @Bean
//    @ConditionalOnClass(value = RedisCacheManager.class)
//    public RedissonSpringCacheManager redisCacheBuild(RedissonClient redisClient) throws IOException {
//        if (Objects.isNull(redisCacheConfig)) {
//            throw new IllegalArgumentException("wf.cache.redis.config 缺失配置.");
//        }
//        return new RedissonSpringCacheManager(redisClient, redisCacheConfig.getConfig());
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "wf.cache.serializer.protobuf.enabled", havingValue = "true")
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//        RedisSerializer<Object> redisSerializer = new RedisSerializer<Object>() {
//            @Override
//            public byte[] serialize(Object o) throws SerializationException {
//                //todo 实现protobuf序列化 泛型
//                return null;
//            }
//
//            @Override
//            public Object deserialize(byte[] bytes) throws SerializationException {
//                //todo 实现protobuf反序列化 泛型
//                return null;
//            }
//        };
//        redisTemplate.setDefaultSerializer(redisSerializer);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(redisSerializer);
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(redisSerializer);
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }
//
//}
