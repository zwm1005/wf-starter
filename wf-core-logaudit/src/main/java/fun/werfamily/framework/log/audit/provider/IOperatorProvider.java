package fun.werfamily.framework.log.audit.provider;

import fun.werfamily.framework.log.audit.model.Operator;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/9/28.
 */
public interface IOperatorProvider {
    /**
     * 解析用户信息
     * @param request
     * @return
     */
    Operator provider(HttpServletRequest request, Object[] params);

}
