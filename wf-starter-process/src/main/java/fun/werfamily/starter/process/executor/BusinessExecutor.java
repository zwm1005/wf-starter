package fun.werfamily.starter.process.executor;


import fun.werfamily.starter.process.model.BusinessContext;
import fun.werfamily.starter.process.model.BusinessModel;
import fun.werfamily.starter.process.model.BusinessType;
import fun.werfamily.starter.process.model.InnerResult;

/**
 * 业务执行器
 *
 * @AuthorMr.WenMing
 */
public interface BusinessExecutor<C extends BusinessContext, M extends BusinessModel> {

    /**
     * 业务执行方法
     *
     * @param context
     * @return
     */
    InnerResult<M> execute(C context);

    /**
     * 获取业务类型
     *
     * @return
     */
    BusinessType getBusinessType();

}
