//package fun.werfamily.starter.cache.manager;
//
//import com.github.benmanes.caffeine.cache.Caffeine;
//import fun.werfamily.starter.cache.config.CaffeineCacheConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.interceptor.KeyGenerator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Author: Mr.WenMing
// * @since: 2021/8/1
// */
//@Configuration
//@ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine")
//@EnableCaching
//@ImportAutoConfiguration(classes = CaffeineCacheConfig.class)
//public class CaffeineCacheManager {
//
//    @Autowired(required = false)
//    private CaffeineCacheConfig caffeineCacheConfig;
//
//    @Bean
//    @ConditionalOnClass(CaffeineCacheManager.class)
//    public KeyGenerator keyGenerator() {
//        return (o, method, objects) -> {
//            StringBuilder sb = new StringBuilder();
//            sb.append(o.getClass().getName());
//            sb.append(method.getName());
//            for (Object obj : objects) {
//                sb.append(obj.toString());
//            }
//            return sb.toString();
//        };
//    }
//
//    /**
//     * 配置缓存管理器
//     *
//     * @return 缓存管理器
//     */
//    @Bean("caffeineCacheManager")
//    @ConditionalOnClass(CaffeineCacheConfig.class)
//    public CacheManager cacheManager() {
//        org.springframework.cache.caffeine.CaffeineCacheManager cacheManager = new org.springframework.cache.caffeine.CaffeineCacheManager();
//        if (Objects.isNull(caffeineCacheConfig)) {
//            throw new IllegalArgumentException("wf.cache.caffeine.config 缺失配置.");
//        }
//        cacheManager.setCaffeine(Caffeine.newBuilder()
//                // 设置最后一次写入或访问后经过固定时间过期
//                .expireAfterAccess(caffeineCacheConfig.getDuration(), TimeUnit.SECONDS)
//                // 初始的缓存空间大小
//                .initialCapacity(caffeineCacheConfig.getInitCapacity())
//                // 缓存的最大条数
//                .maximumSize(caffeineCacheConfig.getMaximumSize()));
//        return cacheManager;
//    }
//
//}
