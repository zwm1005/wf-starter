package fun.werfamily.framework.log.audit.model;

/**
 * @Author Mr.WenMing
 * @date 2023/12/1 13:51
 */
public enum LogGroupEnum {
    DEFAULT("default", "默认"),
    STORE("store", "门店"),
    USER("user", "用户"),
    GOODS("goods", "商品"),
    ORDER("order", "订单"),
    PROMOTER("promoter", "省总"),
    AGENT("agent", "市总"),
    SUPPLIER("supplier", "供应商"),
    DEVICE("devMr.WenMing", "设备"),
    WORK_ORDER("workOrder", "工单"),
    ;
    private String groupName;
    private String desc;

    public String getGroupName() {
        return groupName;
    }

    LogGroupEnum(String groupName, String desc) {
        this.groupName = groupName;
        this.desc = desc;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
