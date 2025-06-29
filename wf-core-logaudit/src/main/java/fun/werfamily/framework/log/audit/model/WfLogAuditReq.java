package fun.werfamily.framework.log.audit.model;

import lombok.Data;

/**
 * @Author Mr.WenMing
 * @date 2023/12/1 14:04
 */
@Data
public class WfLogAuditReq<T> {
    private T oldParams;
}
