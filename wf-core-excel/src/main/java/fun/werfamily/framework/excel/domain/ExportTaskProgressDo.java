package fun.werfamily.framework.excel.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 导出任务进度
 * </p>
 *
 * @Author Mr.WenMing
 * @since 2022-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("export_task_progress")
public class ExportTaskProgressDo extends Model<ExportTaskProgressDo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户类型 1：会员 2.省总 3.市总 3.门店 4.SAAS用户
     */
    private String userType;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 业务ID
     */
    private String businessId;
    /**
     * 导出类型
     */
    private String exportType;
    /**
     * 导出附件URL
     */
    private String fileUrl;
    /**
     * 云存储key
     */
    private String objectKey;
    /**
     * 导出进度
     */
    private Integer exportProgress;
    /**
     * 导出总数
     */
    private Integer exportTotal;
    /**
     * 状态 0 中断 1 进行中 2 已完成 3 已过期
     */
    private Integer status;
    /**
     * 导出最大总数
     */
    private Integer exportMaxTotal;
    /**
     * 导出分页数量
     */
    private Integer exportPageSize;
    /**
     * 间隔执行SQL时间
     */
    private Integer exportIntervalTime;
    /**
     * 文件过期时间
     */
    private Date expireTime;
    /**
     * 获取数据参数
     */
    private String dataParam;
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
