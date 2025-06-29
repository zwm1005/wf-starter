package fun.werfamily.delayqueue.job;

import fun.werfamily.delayqueue.executor.JobExecutor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Mr.WenMing
 * @since: 2021/8/23
 */
@Data
public class DelayJob<P> implements Serializable {

    private static final long serialVersionUID = 2979243570150666892L;

    /**
     * 具体执行实例实现,必须实现了JobExecutor接口
     */
    private Class<? extends JobExecutor<DelayJob<P>, P>> Service;

    /**
     * job执行参数
     */
    private P jobParams;

    public DelayJob(Class<? extends JobExecutor<DelayJob<P>, P>> Service, P jobParams) {
        this.jobParams = jobParams;
        this.Service = Service;
    }
}