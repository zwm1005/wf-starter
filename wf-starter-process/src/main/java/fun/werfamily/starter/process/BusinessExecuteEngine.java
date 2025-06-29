package fun.werfamily.starter.process;

import fun.werfamily.starter.process.model.BusinessContext;
import fun.werfamily.starter.process.model.BusinessModel;
import fun.werfamily.starter.process.model.InnerResult;

/**
 * 业务执行引擎接口
 *
 * @AuthorMr.WenMing
 */
public interface BusinessExecuteEngine<C extends BusinessContext, M extends BusinessModel> {

    /**
     * 业务引擎执行方法
     *
     * @param context 业务上下文
     * @return 业务执行结果
     */
    InnerResult<M> execute(C context);

}
