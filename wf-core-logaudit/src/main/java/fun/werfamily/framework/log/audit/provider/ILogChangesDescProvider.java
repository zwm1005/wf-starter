package fun.werfamily.framework.log.audit.provider;

/**
 * @Author Mr.WenMing
 * @date 2023/12/4 16:28
 */
public interface ILogChangesDescProvider<T> {
    /**
     * 变更内容描述解析器
     * @param oldParams
     * @param params
     * @return
     */
    String providerChangesDesc(T oldParams, T params);
}
