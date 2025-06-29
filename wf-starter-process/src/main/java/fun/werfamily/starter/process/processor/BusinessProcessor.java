package fun.werfamily.starter.process.processor;

import fun.werfamily.starter.process.model.BusinessContext;
import fun.werfamily.starter.process.model.BusinessModel;
import fun.werfamily.starter.process.model.InnerResult;

/**
 * 业务执行器接口
 *
 * @AuthorMr.WenMing
 */
public interface BusinessProcessor<C extends BusinessContext, M extends BusinessModel> {

    /**
     * 业务执行器执行方法
     *
     * @param context 业务上下文
     * @return 业务执行结果
     */
    InnerResult<M> process(C context);

    /**
     * 获取执行器名称
     *
     * @return
     */
    String getProcessorName();

}
