package fun.werfamily.framework.log.audit.model;

import lombok.*;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/9/28.
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operator {

    /**
     * 操作人ID
     */
    private String operatorId;
    /**
     * 操作人类型
     */
    private String operatorType;
    /**
     * 操作人name
     */
    private String operatorName;

    /**
     * 操作人手机号
     */
    private String operatorPhone;

    /**
     * 租户id
     */
    private String tenantId;
}
