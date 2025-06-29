package fun.werfamily.framework.excel.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 导出任务进度
 * </p>
 *
 * @Author Mr.WenMing
 * @since 2022-07-11
 */
@Data
@ApiModel
public class ExportTaskProgressDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 任务ID
     */
    @ApiModelProperty(value = "导出任务ID")
    private String taskId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "导出任务名称")
    private String taskName;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 用户类型 1：会员 2.省总 3.市总 3.门店 4.SAAS用户
     */
    @ApiModelProperty(value = "用户类型")
    private String userType;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
    /**
     * 业务ID
     */
    @ApiModelProperty(value = "业务ID")
    private String businessId;
    /**
     * 导出类型
     */
    @ApiModelProperty(value = "导出类型")
    private String exportType;
    /**
     * 导出附件URL
     */
    @ApiModelProperty(value = "导出附件URL")
    private String fileUrl;
    /**
     * 云存储key
     */
    @ApiModelProperty(value = "云存储key")
    private String objectKey;
    /**
     * 导出进度
     */
    @ApiModelProperty(value = "导出进度百分比值")
    private Double exportProgressBar;
    /**
     * 导出进度
     */
    @ApiModelProperty(value = "导出进度")
    private Integer exportProgress;
    /**
     * 导出总数
     */
    @ApiModelProperty(value = "导出总数")
    private Integer exportTotal;
    /**
     * 状态 0 中断 1 进行中 2 已完成 3 已过期
     */
    @ApiModelProperty(value = "状态状态 0 中断 1 进行中 2 已完成 3 已过期")
    private Integer status;
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
    /**
     * 文件过期时间
     */
    @ApiModelProperty(value = "文件过期时间")
    private Date expireTime;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 修改者
     */
    private String updateBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 0:未删除，1:已删除
     */
    private Boolean isDeleted;

}
