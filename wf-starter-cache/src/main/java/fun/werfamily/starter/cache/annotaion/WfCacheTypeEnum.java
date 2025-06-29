package fun.werfamily.starter.cache.annotaion;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName
 * @Description
 */
public enum WfCacheTypeEnum {

    REDIS("redis", "redis缓存"),
    REDIS_HASH("redisHash", "redisHash缓存"),
//    LOCAL("redis", "本地缓存"),
    ;

    public final String val;
    public final String desc;

    public String getVal() {
        return val;
    }

    public String getDesc() {
        return desc;
    }


    WfCacheTypeEnum(String val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public static WfCacheTypeEnum getByVal(String val) {
        for (WfCacheTypeEnum item :
                WfCacheTypeEnum.values()) {
            if (StringUtils.equals(val, item.val)) {
                return item;
            }
        }
        return null;
    }


}
