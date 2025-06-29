package fun.werfamily.starter.cache.adapter;

import com.alibaba.fastjson.JSON;
import fun.werfamily.starter.cache.annotaion.WfCache;
import fun.werfamily.starter.cache.annotaion.WfCacheDel;
import fun.werfamily.starter.cache.annotaion.WfCacheTypeEnum;
import fun.werfamily.starter.cache.config.WfCacheConfig;
import fun.werfamily.starter.cache.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2021/12/14.
 */
@Component
@Slf4j
@Aspect
public class CacheAdapter {

    @Autowired
    private RedisService redisService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private WfCacheConfig wfCacheConfig;
    /**
     * 默认无参key后缀
     */
    public static final String PO_LIST_SUFFIX = "ALL";

    /**
     * 字符串拼接符
     */
    public static final String SPLIT_SUFFIX = "_";

    /**
     * hash缓存后缀
     */
    public static final String REDIS_HASH_SUFFIX = "HASH";

    /**
     * 缓存处理接口
     *
     * @param jp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(fun.werfamily.starter.cache.annotaion.WfCache)")
    public Object ServiceCacheHandler(ProceedingJoinPoint jp) throws Throwable {
        if(!wfCacheConfig.getRedisAnnoSwitch()) {
            return jp.proceed(jp.getArgs());
        }

        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        WfCache wfCache = methodSignature.getMethod().getAnnotation(WfCache.class);
        Class returnType = methodSignature.getMethod().getReturnType();

        if (wfCache.type() == WfCacheTypeEnum.REDIS_HASH) {
            return loadHashCache(jp, wfCache, returnType);
//        } else if (wfCache.type() == WfCacheTypeEnum.LOCAL) {
//            //暂未实现、使用到的时候在实现
//            return jp.proceed(jp.getArgs());
        } else if(wfCache.type() == WfCacheTypeEnum.REDIS) {
            return loadRedisCache(jp, wfCache, returnType);
        }else {
            return jp.proceed(jp.getArgs());
        }
    }

    @Around("@annotation(fun.werfamily.starter.cache.annotaion.WfCacheDel)")
    public Object ServiceCacheClearHandler(ProceedingJoinPoint jp) throws Throwable {
        Object o = null;
        try {
            o = jp.proceed(jp.getArgs());
            delWfCache(jp);
        }catch (Throwable e) {
           throw e;
        }
        return o;
    }

    /**
     * 获取缓存Key
     *
     * @param jp
     * @param redisKey
     * @param priorityKeys
     * @return
     */
    private String getCacheKey(ProceedingJoinPoint jp, String redisKey, String[] priorityKeys) throws Exception{
        Object[] params = jp.getArgs();
        //如果方法没有参数就采用配置拼接
        if (params == null || params.length == 0) {
            redisKey += PO_LIST_SUFFIX;
        } else {
            redisKey += getVariableKey(params, priorityKeys);
        }
        return redisKey;
    }

    /**
     * 根据方法参数获取缓存Key可变后缀
     *
     * @param params
     * @return
     */
    private String getVariableKey(Object[] params, String[] priorityKeys) throws Exception {
        String variableKey = "";
        for (int i = 0; i < params.length; i++) {
            if (priorityKeys != null && priorityKeys.length > 0) {
                Object o = params[i];
                for (String key : priorityKeys) {
                    if (StringUtils.isNotBlank(key)) {
                        variableKey += PropertyUtils.getProperty(o, key) + SPLIT_SUFFIX;
                    }
                }
            } else {
                variableKey += params[i] + SPLIT_SUFFIX;
            }
        }

        return variableKey.substring(0, variableKey.length() - 1);
    }

    /**
     * redis 缓存处理
     *
     * @param jp
     * @param wfCache
     * @param returnType
     * @return
     * @throws Throwable
     */
    private Object loadRedisCache(ProceedingJoinPoint jp, WfCache wfCache, Class returnType) throws Throwable {
        String redisKey = getCacheKey(jp, wfCache.value(), wfCache.priorityKeys());
        Object rvt = null;

        if(StringUtils.isBlank(redisKey)) {
            return jp.proceed(jp.getArgs());
        }

        String cacheData = redisService.get(redisKey);

        if (StringUtils.isNotBlank(cacheData)) {
            try {
                if (returnType == List.class) {
                    rvt = JSON.parseArray(cacheData, wfCache.clazz());
                } else {
                    rvt = JSON.parseObject(cacheData, wfCache.clazz());
                }
            } catch (Exception e) {
                log.error("[WfCache]缓存读取异常[以兼容] key = {}", redisKey, e);
                rvt = jp.proceed(jp.getArgs());
            }
        }else {
            rvt = jp.proceed(jp.getArgs());
            try {
                if (rvt != null) {
                    redisService.set(redisKey, JSON.toJSONString(rvt), wfCache.timeOut());
                }
            } catch (Exception e) {
                log.error("[WfCache]缓存设置异常[以兼容] key = {}", redisKey, e);
            }
        }

        return rvt;
    }

    /**
     * redis hash缓存处理
     *
     * @param jp
     * @param wfCache
     * @param returnType
     * @return
     * @throws Throwable
     */
    private Object loadHashCache(ProceedingJoinPoint jp, WfCache wfCache, Class returnType) throws Throwable {
        Object[] params = jp.getArgs();
        //无参数不支持HASH缓存
        if (params == null || params.length == 0) {
            return jp.proceed(jp.getArgs());
        }
        String redisKey = wfCache.value() + REDIS_HASH_SUFFIX;
        String hashKey = getVariableKey(params, wfCache.priorityKeys());
        Object rvt = null;

        try {
            if (redisTemplate.opsForHash().hasKey(redisKey, hashKey) != null
                    && redisTemplate.opsForHash().hasKey(redisKey, hashKey)) {
                String value = (String) redisTemplate.opsForHash().get(redisKey, hashKey);

                if (returnType == List.class) {
                    rvt = JSON.parseArray(value, wfCache.clazz());
                } else {
                    rvt = JSON.parseObject(value, wfCache.clazz());
                }
                return rvt;
            }
        } catch (Exception e) {
            log.error("[WfCache]缓存读取异常[以兼容] key = {}", redisKey, e);
        }

        //执行被代理方法
        rvt = jp.proceed(jp.getArgs());
        try {
            if (rvt != null) {
                redisTemplate.opsForHash().put(redisKey, hashKey, JSON.toJSONString(rvt));
                redisTemplate.expire(redisKey, wfCache.timeOut(), TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            log.error("[WfCache]缓存读取异常[以兼容] key = {}", redisKey, e);
        }

        return rvt;
    }

    private boolean delWfCache(ProceedingJoinPoint jp) {
        boolean result = true;
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        WfCacheDel wfCacheDel = methodSignature.getMethod().getAnnotation(WfCacheDel.class);

        Set<String> keys = new HashSet<>();
        try {
            Method method = methodSignature.getMethod();
            Class c = method.getDeclaringClass();
            WfCache wfCache = (WfCache) c.getAnnotation(WfCache.class);
            if (wfCache != null && StringUtils.isNotEmpty(wfCache.value())) {
                String cacheKey = wfCache.value();
                String[] cacheKeys = wfCache.values();
                //清除主逻辑缓存
                if (StringUtils.isNotEmpty(cacheKey)) {
                    keys.addAll(clearCache(cacheKey));
                }
                //清除依赖缓存
                List<String> cacheKeyList = Arrays.asList(cacheKeys);
                if (!CollectionUtils.isEmpty(cacheKeyList)) {
                    for (String key : cacheKeyList) {
                        keys.addAll(clearCache(key));
                    }
                }
            }

            Object[] params = jp.getArgs();
            if (wfCacheDel.type() == WfCacheTypeEnum.REDIS_HASH) {
                String redisKey = wfCache.value() + REDIS_HASH_SUFFIX;
                String hashKey = getVariableKey(params, wfCache.priorityKeys());
                if (redisTemplate.opsForHash().hasKey(redisKey, hashKey) != null
                        && redisTemplate.opsForHash().hasKey(redisKey, hashKey)) {
                    redisTemplate.opsForHash().delete(redisKey, hashKey);
                }
//        } else if (wfCacheDel.type() == WfCacheTypeEnum.LOCAL) {
//            //暂未实现、使用到的时候在实现
            } else if(wfCacheDel.type() == WfCacheTypeEnum.REDIS) {
                String redisKey = getCacheKey(jp, wfCacheDel.value(), wfCacheDel.priorityKeys());
                if(redisService.exists(redisKey)) {
                    redisService.del(redisKey);
                    log.info("[WfCache]删除缓存成功 redisKey = {}", redisKey);
                }
            }
        } catch (Exception e) {
            result = false;
            log.error("【告警 [WfCache]缓存清除失败！】 keys = {} ", keys, e);
        }
        return result;
    }

    private Set<String> clearCache(String cacheKey) {
        Set<String> keys = new HashSet<>();
        if (StringUtils.isNotEmpty(cacheKey)) {
            if (cacheKey.endsWith(":")) {
                cacheKey = cacheKey.substring(0, cacheKey.length() - 1);
            }
            cacheKey += "*";
            keys = redisService.keys(cacheKey);
            if (!CollectionUtils.isEmpty(keys)) {
                redisService.del(keys);
                log.info("[WfCache]缓存清除成功！ cacheKey = {} ", cacheKey);
            } else {
                log.warn("[WfCache]缓存清除无缓存！ cacheKey = {}", cacheKey);
            }
        }
        return keys;
    }

}
