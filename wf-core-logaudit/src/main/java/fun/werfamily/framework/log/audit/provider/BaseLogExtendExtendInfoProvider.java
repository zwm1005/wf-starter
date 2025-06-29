package fun.werfamily.framework.log.audit.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.WenMing
 * @date 2023/12/1 14:46
 */
@Component
@Slf4j
public class BaseLogExtendExtendInfoProvider<T> implements ILogExtendInfoProvider {

    @Override
    public Object provideExtendInfo(String businessId) {
        return null;
    }
}
