package fun.werfamily.starter.tenant;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author Mr.WenMing
 */
@Slf4j
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String TENANT_ID = "tenantId";

    private static final String IS_CHECK_TENANT_ID = "isCheckTenantId";

    @Override
    public void apply(RequestTemplate template) {
        // 获取登录用户信息取得租户信息放入header
//        UserLoginInfo currentUser = LoginUserHolder.getCurrentUser();
//        if(currentUser != null && currentUser.getBaseUser() != null){
//            template.header(TENANT_ID , currentUser.getBaseUser().getTenantId());
//            template.header(IS_CHECK_TENANT_ID , "true");
//        }
    }
}