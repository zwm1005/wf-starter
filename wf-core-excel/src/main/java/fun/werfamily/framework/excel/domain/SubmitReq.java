package fun.werfamily.framework.excel.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/11.
 */
@Data
@ApiModel
public class SubmitReq {

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称",required = true)
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID",required = true)
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    /**
     * 用户类型 1：会员 2.省总 3.市总 3.门店 4.SAAS用户
     */
    @NotBlank(message = "用户类型不能为空")
    @ApiModelProperty(value = "用户类型 1：会员 2.省总 3.市总 3.门店 4.SAAS用户",required = true)
    private String userType;
    /**
     * 租户ID
     */
    @NotBlank(message = "租户ID不能为空")
    @ApiModelProperty(value = "租户ID",required = true)
    private String tenantId;
    /**
     * 业务ID
     */
    @NotBlank(message = "业务ID不能为空")
    @ApiModelProperty(value = "业务ID",required = true)
    private String businessId;
    /**
     * 导出类型
     */
    @NotBlank(message = "导出类型不能为空")
    @ApiModelProperty(value = "导出类型",required = true)
    private String exportType;
    /**
     * 获取数据参数
     */
    @ApiModelProperty(value = "获取数据参数")
    private String dataParam;
    /**
     * 间隔执行SQL时间
     */
    @ApiModelProperty(value = "间隔执行SQL时间")
    private Integer exportIntervalTime;
    /**
     * 导出最大总数
     */
    @ApiModelProperty(value = "导出最大总数")
    private Integer exportMaxTotal;
    /**
     * 导出分页数量
     */
    @ApiModelProperty(value = "导出分页数量")
    private Integer exportPageSize;

    @ApiModelProperty(value = "导出备注")
    private String remarks;

}
