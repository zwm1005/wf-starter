package fun.werfamily.framework.log.audit.provider;
/**
 * @Author Mr.WenMing
 * @date 2023/12/1 14:43
 */
public interface ILogExtendInfoProvider<T> {
    /**
     * 根据业务主键id 提供日志扩展信息
     * @param businessId
     * @return
     */
    T provideExtendInfo(String businessId);
}
