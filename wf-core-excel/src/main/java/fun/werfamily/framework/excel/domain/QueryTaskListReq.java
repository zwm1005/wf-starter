package fun.werfamily.framework.excel.domain;

import lombok.Data;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
@Data
public class QueryTaskListReq {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户类型 1：会员 2.省总 3.市总 3.门店 4.SAAS用户
     */
    private String userType;
    /**
     * 业务ID
     */
    private String businessId;
    /**
     * 导出类型
     */
    private String exportType;

    private int pageNum =1;

    private int pageSize = 10;

}
