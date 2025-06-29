package fun.werfamily.framework.log.audit.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.util.Date;

/**
 * @Author Mr.WenMing
 * @date 2023/12/1 13:51
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessAuditLog {
    /**
     * mongo主键
     */
    private String docId;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 业务主键
     */
    private String businessId;
    /**
     * 业务名称
     */
    private String businessName;
    /**
     * 日志组：store-门店
     */
    private String logGroup;
    /**
     * 更改前数据
     */
    private String oldParams;
    /**
     * 更改后数据
     */
    private String params;

    /**
     * 变更内容描述
     */
    private String changesDesc;
    /**
     * 返回结果
     */
    private String result;
    /**
     * 客户端ip
     */
    private String clientIp;
    /**
     * 服务端ip
     */
    private String serverIp;
    /**
     * 操作人ID
     */
    private String operatorId;
    /**
     * 操作人名称
     */
    private String operatorName;
    /**
     * 操作人手机号
     */
    private String operatorPhone;
    /**
     * 操作人类型
     */
    private String operatorType;
    /**
     * 操作描述
     */
    private String operationDesc;
    /**
     * 操作时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date operationTime;
    /**
     * 接口路径
     */
    private String apiPath;
    /**
     * API名称
     */
    private String apiName;
    /**
     * api描述
     */
    private String apiDesc;

    /**
     * 服务名称
     */
    private String ServiceName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 扩展信息
     */
    private Object extendInfo;
}
