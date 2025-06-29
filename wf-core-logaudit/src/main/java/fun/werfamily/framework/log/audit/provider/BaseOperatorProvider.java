package fun.werfamily.framework.log.audit.provider;

import fun.werfamily.framework.log.audit.model.BaseUser;
import fun.werfamily.framework.log.audit.model.MemberTypeEnum;
import fun.werfamily.framework.log.audit.model.Operator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/10/8.
 */
@Component("baseOperatorProvider")
@Slf4j
public class BaseOperatorProvider implements IOperatorProvider{

    @Override
    public Operator provider(HttpServletRequest request, Object[] params) {

        BaseUser baseUser = getUser();
        if(baseUser != null) {
            Operator operator = Operator.builder()
                    .operatorType(baseUser.getMemberType() == null ? String.valueOf(MemberTypeEnum.USER.getMemberType()) : String.valueOf(baseUser.getMemberType()))
                    .operatorName(baseUser.getName())
                    .operatorPhone(baseUser.getUserName())
                    .tenantId(baseUser.getTenantId())
                    .build();

            if(null == baseUser.getMemberType() || MemberTypeEnum.USER.getMemberType().equals(baseUser.getMemberType())) {
                operator.setOperatorId(baseUser.getUserId()+"");
            }else if(MemberTypeEnum.MEMBER.getMemberType().equals(baseUser.getMemberType())) {
                operator.setOperatorId(baseUser.getMemberId()+"");
            }else if(MemberTypeEnum.PLATFORM_MEMBER.getMemberType().equals(baseUser.getMemberType())) {
                operator.setOperatorId(baseUser.getPid()+"");
            }
            return operator;
        }
        return Operator.builder().build();
    }

    public BaseUser getUser() {
        try {
            // 返回当前登录人的信息
            return null;
        }catch (Exception e) {
            log.warn("log-audit 获取登录用户信息失败 ", e);
            return null;
        }
    }
}
