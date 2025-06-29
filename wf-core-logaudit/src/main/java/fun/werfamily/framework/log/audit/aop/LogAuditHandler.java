package fun.werfamily.framework.log.audit.aop;

import com.alibaba.fastjson.JSON;
import fun.werfamily.framework.log.audit.annotaion.WfLogAudit;
import fun.werfamily.framework.log.audit.model.BusinessAuditLog;
import fun.werfamily.framework.log.audit.model.WfLogAuditReq;
import fun.werfamily.framework.log.audit.provider.ILogChangesDescProvider;
import fun.werfamily.framework.log.audit.provider.ILogExtendInfoProvider;
import fun.werfamily.framework.log.audit.util.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/10/8.
 */
@Component
@Slf4j
public class LogAuditHandler {

    /**
     * 处理日志及提交
     *
     * @param businessAuditLog
     */
    public void handle(BusinessAuditLog businessAuditLog, WfLogAudit wfLogAudit, Object[] params) {
        try {
            List<Object> reqParamsList = Arrays.stream(params).filter(o -> o instanceof WfLogAuditReq).collect(Collectors.toList());
            // 解析变更内容及日志扩展信息
            if (!CollectionUtils.isEmpty(reqParamsList)) {
                Object req = reqParamsList.get(0);
                if (null != req) {
                    ILogExtendInfoProvider logInfoProvider = (ILogExtendInfoProvider) ContextUtil.getBean(wfLogAudit.extendInfoProvider());
                    ILogChangesDescProvider changesDescProvider = (ILogChangesDescProvider) ContextUtil.getBean(wfLogAudit.changesDescProvider());
                    // 日志扩展信息
                    businessAuditLog.setExtendInfo(logInfoProvider.provideExtendInfo(businessAuditLog.getBusinessId()));
                    Object newParams = null;
                    Object oldParams = null;
                    if (StringUtils.isNotEmpty(businessAuditLog.getParams())) {
                        newParams = JSON.parseObject(businessAuditLog.getParams(), req.getClass());
                    }
                    if (StringUtils.isNotEmpty(businessAuditLog.getOldParams())) {
                        oldParams = JSON.parseObject(businessAuditLog.getOldParams(), req.getClass());
                    }
                    // 变更内容描述
                    String changesDesc = changesDescProvider.providerChangesDesc(oldParams, newParams);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(businessAuditLog.getOperationDesc());
                    if (StringUtils.isNotEmpty(changesDesc)) {
                        stringBuilder.append(":\r\n").append(changesDesc);
                    }
                    businessAuditLog.setChangesDesc(stringBuilder.toString());
                    log.info("审计日志处理后{}", JSON.toJSONString(businessAuditLog));
                }
            }
            // TODO 调用日志服务，保存日志
            log.info("[日志审计]上报");
        } catch (Throwable e) {
            log.error("日志审计异步处理异常 {}", businessAuditLog, e);
        }
    }
}
