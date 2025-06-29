package fun.werfamily.starter.process.impl;

import fun.werfamily.starter.process.BusinessExecuteEngine;
import fun.werfamily.starter.process.exception.ProcessException;
import fun.werfamily.starter.process.executor.BusinessExecutor;
import fun.werfamily.starter.process.model.BusinessContext;
import fun.werfamily.starter.process.model.BusinessModel;
import fun.werfamily.starter.process.model.InnerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务执行引擎接口实现
 *
 * @AuthorMr.WenMing
 */
public class BusinessExecuteEngineImpl implements BusinessExecuteEngine {
    /**
     * 日志打印器
     */
    private final static Logger logger = LoggerFactory.getLogger(BusinessExecuteEngineImpl.class);

    /**
     * 业务执行器Map key为实现BusinessType.getCode()
     */
    private Map<String, BusinessExecutor<BusinessContext, BusinessModel>> businessExecutorMap = new HashMap<String, BusinessExecutor<BusinessContext, BusinessModel>>();

    /**
     * 业务引擎执行方法
     *
     * @param context 业务上下文
     * @return 业务执行结果
     */
    @Override
    public InnerResult<BusinessModel> execute(BusinessContext context) {
        BusinessExecutor executor = businessExecutorMap.get(context.getBusinessType().getCode());
        if (executor == null) {
            throw new RuntimeException("没有找到对应的执行器。BusinessType = " + context.getBusinessType());
        }

        try {

            logger.info("业务引擎执行。BusinessContext = {}", context);

            //执行业务
            InnerResult<BusinessModel> innerResult = executor.execute(context);

            if (!innerResult.getSuccess()) {
                logger.warn("业务引擎执行失败。BusinessContext = {}，innerResult={}", context,
                        innerResult);
            }
            return innerResult;
        } catch (ProcessException exception) {

            logger.warn("业务引擎执行出现业务异常。BusinessContext = {}", context);
            return new InnerResult(exception.getErrorContext());

        } finally {

        }
    }

    /**
     * Setter method for property <tt>counterType</tt>.
     *
     * @param businessExecutorMap value to be assigned to property businessExecutorMap
     */
    public void setBusinessExecutorMap(Map<String, BusinessExecutor<BusinessContext, BusinessModel>> businessExecutorMap) {
        this.businessExecutorMap = businessExecutorMap;
    }
}
