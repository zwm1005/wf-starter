package fun.werfamily.framework.log.audit.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.util.Date;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/9/28.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogAuditInfo {
    /**
     * 服务名称
     */
    private String ServiceName;

    /**
     * 操作时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss.SSS")
    private Date operationTime;
    /**
     * 客户端信息
     */
    private String clientIp;
    /**
     * 服务端信息
     */
    private String serverIp;
    /**
     * 操作人ID
     */
    private String operatorId;
    /**
     * 操作人类型
     */
    private String operatorType;
    /**
     * 操作人名称
     */
    private String operatorName;
    /**
     * api
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
     * 入参列表
     */
    private String params;
    /**
     * 返回结果
     */
    private String result;

    /**
     * 租户id
     */
    private String tenantId;

}
