package fun.werfamily.framework.log.audit.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 基础用户
 * @Author Mr.WenMing
 */
@Data
public class BaseUser implements Serializable {
    /**
     * 门店员工主键id
     */
    private Integer userId;
    /**
     * 平台会员ID
     */
    private String pid;
    /**
     * 租户会员ID
     */
    private Integer memberId;
    /**
     * 用户类型 1：user 2:member（租户会员） 3:平台会员
     * MemberType
     */
    private Integer memberType;
    /**
     * 客户端Code
     */
    private String clientCode;

    /**
     * 数据创建时间
     */
    protected LocalDateTime createDate = LocalDateTime.now();
    /**
     * 自适应的id，系统标识所对应的系统主键id
     */
    private Integer adaptId;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 系统登录名称-整个唯一
     * */
    private String userName;
    /**
     * 真实姓名-可重复
     */
    private String name;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 用户头像
     */
    private String headImg;
    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 账号类型 user表中type
     */
    private Integer type;
}
