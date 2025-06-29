package fun.werfamily.framework.log.audit.aop;

import com.alibaba.fastjson.JSON;
import fun.werfamily.framework.log.audit.annotaion.BusinessId;
import fun.werfamily.framework.log.audit.annotaion.WfLogAudit;
import fun.werfamily.framework.log.audit.config.LogAuditConfig;
import fun.werfamily.framework.log.audit.config.ServiceNameDefaultConfig;
import fun.werfamily.framework.log.audit.model.BusinessAuditLog;
import fun.werfamily.framework.log.audit.model.LogAuditInfo;
import fun.werfamily.framework.log.audit.model.Operator;
import fun.werfamily.framework.log.audit.model.WfLogAuditReq;
import fun.werfamily.framework.log.audit.provider.IOperatorProvider;
import fun.werfamily.framework.log.audit.util.ContextUtil;
import fun.werfamily.framework.log.audit.util.IPUtils;
import fun.werfamily.framework.log.audit.util.WebUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/9/28.
 */
@Component
@Slf4j
@Aspect
public class LogAuditAop {

    @Autowired
    private LogAuditConfig logAuditConfig;

    @Autowired
    private ServiceNameDefaultConfig ServiceNameDefaultConfig;

    @Autowired
    private LogAuditHandler logAuditHandler;

    private final Executor executor = new ThreadPoolExecutor(5, 10, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5000), r -> new Thread(r, "audit-log-" + r.hashCode()), new ThreadPoolExecutor.DiscardPolicy());

    /**
     * 日志审计AOP处理
     *
     * @param jp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(fun.werfamily.framework.log.audit.annotaion.WfLogAudit)")
    public Object ServiceCacheHandler(ProceedingJoinPoint jp) throws Throwable {
        Date operationTime = new Date();
        Object returnObj = jp.proceed(jp.getArgs());
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            Signature signature = jp.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            WfLogAudit wfLogAudit = methodSignature.getMethod().getAnnotation(WfLogAudit.class);
            // 构建日志
            BusinessAuditLog businessAuditLog = buildBusinessAuditLog(jp.getArgs(), request, wfLogAudit, returnObj, methodSignature, operationTime);
            executor.execute(() -> {
                logAuditHandler.handle(businessAuditLog, wfLogAudit, jp.getArgs());
            });
        } catch (Throwable e) {
            log.error("日志审计构建失败 ", e);
        }
        return returnObj;
    }

    /**
     * @param params
     * @param request
     * @param wfLogAudit
     * @param returnObj
     * @return
     */
    private LogAuditInfo buildLogAuditInfo(Object[] params, HttpServletRequest request, WfLogAudit wfLogAudit, Object returnObj) {
        IOperatorProvider iOperatorProvider = ContextUtil.getBean(wfLogAudit.providerBean());
        Operator operator = iOperatorProvider.provider(request, params);

        LogAuditInfo logAuditInfo = LogAuditInfo.builder().operationTime(new Date())
                .operatorId(operator.getOperatorId())
                .operatorType(operator.getOperatorType())
                .operatorName(operator.getOperatorName())
                .tenantId(operator.getTenantId())
                .apiName(wfLogAudit.value())
                .apiDesc(wfLogAudit.desc())
                .ServiceName(StringUtils.isEmpty(logAuditConfig.getServiceName()) ? ServiceNameDefaultConfig.getName() : logAuditConfig.getServiceName())
                .result(JSON.toJSONString(returnObj))
                .build();

        if (params != null && params.length > 0 && params[0] instanceof HttpServletRequest) {
            HttpServletRequest paramRequest = (HttpServletRequest) params[0];
            Map<String, String> rp = WebUtils.getParameters(paramRequest);
            logAuditInfo.setParams(JSON.toJSONString(rp));
            log.info(" buildLogAuditInfo httpreq: {} ", JSON.toJSONString(logAuditInfo));
            if ("POST".equalsIgnoreCase(paramRequest.getMethod())) {
                String body = WebUtils.getBody(paramRequest);
                logAuditInfo.setParams(body);
                log.info(" buildLogAuditInfo post: {} ", JSON.toJSONString(logAuditInfo));
            }
        } else {
            if (params != null) {
                if (ArrayUtils.isNotEmpty(params)) {
                    List<Object> logArgs = Arrays.stream(params).filter(arg -> (
                                    !(arg instanceof HttpServletRequest)
                                            && !(arg instanceof HttpServletResponse)
                                            && !(arg instanceof MultipartFile)
                                            && !(arg instanceof BeanPropertyBindingResult)))
                            .collect(Collectors.toList());
                    log.info("Request Args  : {}", JSON.toJSONString(logArgs));
                    logAuditInfo.setParams(JSON.toJSONString(logArgs));
                } else {
                    log.info("Request Args  : null");
                }
            } else {
                logAuditInfo.setParams(null);
            }
            log.info(" buildLogAuditInfo: {} ", JSON.toJSONString(logAuditInfo));
        }

        if (request != null) {
            logAuditInfo.setServerIp(IPUtils.getLocalIP());
            logAuditInfo.setClientIp(WebUtils.getIP(request));
            logAuditInfo.setApiPath(request.getRequestURI());
        }

        return logAuditInfo;
    }

    private BusinessAuditLog buildBusinessAuditLog(Object[] params, HttpServletRequest request, WfLogAudit wfLogAudit, Object returnObj, MethodSignature methodSignature, Date operationTime) {
        IOperatorProvider iOperatorProvider = ContextUtil.getBean(wfLogAudit.providerBean());
        Operator operator = iOperatorProvider.provider(request, params);
        // 构建日志
        BusinessAuditLog businessAuditLog = buildBusinessAuditLog(request, operator, wfLogAudit, returnObj, operationTime);
        ApiOperation apiOperation = methodSignature.getMethod().getAnnotation(ApiOperation.class);
        if (null != apiOperation) {
            businessAuditLog.setOperationDesc(apiOperation.value());
        }
        // 接口参数不为空
        if (params != null && params.length > 0) {
            // 只保留接口中继承自WfLogAuditReq的第一个参数
            List<Object> wfLogReqParamsList = Arrays.stream(params).filter(o -> o instanceof WfLogAuditReq).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(wfLogReqParamsList)) {
                Object businessReq = wfLogReqParamsList.get(0);
                log.info("buildBusinessAuditLog httpreq: {} ", JSON.toJSONString(businessAuditLog));
                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    try {
                        List<Field> fieldList = getAllFieldList(businessReq.getClass());
                        Field oldParamsField = null;
                        for (Field declaredField : fieldList) {
                            declaredField.setAccessible(true);
                            // 设置businessId字段的值 接口入参取，兼容新增操作回写id场景
                            if (declaredField.isAnnotationPresent(BusinessId.class)) {
                                Object businessId = declaredField.get(businessReq);
                                businessAuditLog.setBusinessId(null == businessId ? null : String.valueOf(businessId));
                            }
                            if ("oldParams".equals(declaredField.getName())) {
                                oldParamsField = declaredField;
                            }
                            if (StringUtils.isEmpty(businessAuditLog.getTenantId()) && "tenantId".equals(declaredField.getName())) {
                                businessAuditLog.setTenantId(null == declaredField.get(businessReq) ? null : String.valueOf(declaredField.get(businessReq)));
                            }
                        }
                        if (null != oldParamsField) {
                            Object oldParamsValue = oldParamsField.get(businessReq);
                            businessAuditLog.setOldParams(null == oldParamsValue ? null : JSON.toJSONString(oldParamsValue));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    businessAuditLog.setParams(JSON.toJSONString(businessReq));
                    log.info("buildBusinessAuditLog post: {} ", JSON.toJSONString(businessAuditLog));
                }
            } else {
                List<Object> logArgs = Arrays.stream(params).filter(arg -> (
                                !(arg instanceof HttpServletRequest)
                                        && !(arg instanceof HttpServletResponse)
                                        && !(arg instanceof MultipartFile)
                                        && !(arg instanceof BeanPropertyBindingResult)))
                        .collect(Collectors.toList());
                log.info("Request params  : {}", JSON.toJSONString(logArgs));
                businessAuditLog.setParams(JSON.toJSONString(logArgs));
                log.info("buildBusinessAuditLog: {} ", JSON.toJSONString(businessAuditLog));
            }
        }
        return businessAuditLog;
    }

    /**
     * 获取所有属性
     * @param reqClass
     * @return
     */
    private List<Field> getAllFieldList(Class reqClass) {
        List<Field> fieldList = new ArrayList<>();
        Field[] declaredFields = reqClass.getDeclaredFields();
        fieldList.addAll(Arrays.asList(declaredFields));
        if (reqClass.getSuperclass() != null) {
            Field[] superFields = reqClass.getSuperclass().getDeclaredFields();
            fieldList.addAll(Arrays.asList(superFields));
        }
        return fieldList;
    }

    /**
     * 构建日志
     * @param request
     * @param operator
     * @param wfLogAudit
     * @param returnObj
     * @param operationTime
     * @return
     */
    private BusinessAuditLog buildBusinessAuditLog(HttpServletRequest request, Operator operator, WfLogAudit wfLogAudit, Object returnObj, Date operationTime){
        BusinessAuditLog businessAuditLog = BusinessAuditLog.builder().operationTime(operationTime)
                .operatorId(operator.getOperatorId())
                .operatorType(operator.getOperatorType())
                .operatorName(operator.getOperatorName())
                .operatorPhone(operator.getOperatorPhone())
                .tenantId(operator.getTenantId())
                .apiName(wfLogAudit.value())
                .apiDesc(wfLogAudit.desc())
                .logGroup(wfLogAudit.group().getGroupName())
                .ServiceName(StringUtils.isEmpty(logAuditConfig.getServiceName()) ? ServiceNameDefaultConfig.getName() : logAuditConfig.getServiceName())
                .result(JSON.toJSONString(returnObj))
                .build();

        if (request != null) {
            businessAuditLog.setServerIp(IPUtils.getLocalIP());
            businessAuditLog.setClientIp(WebUtils.getIP(request));
            businessAuditLog.setApiPath(request.getRequestURI());
        }
        return businessAuditLog;
    }
}
