package fun.werfamily.framework.log.audit.model;

/**
 * @Author Mr.WenMing
 * @date 2022/11/10 17:25
 */
public enum MemberTypeEnum {
    USER(1, "普通用户"),
    MEMBER(2, "普通会员"),
    PLATFORM_MEMBER(3, "平台会员");

    MemberTypeEnum(Integer memberType, String desc) {
        this.memberType = memberType;
        this.desc = desc;
    }

    public Integer getMemberType() {
        return memberType;
    }

    public void setMemberType(Integer memberType) {
        this.memberType = memberType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private Integer memberType;
    private String desc;

}
