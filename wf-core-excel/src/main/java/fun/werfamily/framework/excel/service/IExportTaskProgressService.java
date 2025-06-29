package fun.werfamily.framework.excel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.werfamily.framework.excel.domain.ExportTaskProgressDo;
import fun.werfamily.framework.excel.domain.SubmitReq;

/**
 * <p>
 * 导出任务进度 服务类
 * </p>
 *
 * @Author Mr.WenMing
 * @since 2022-07-11
 */
public interface IExportTaskProgressService extends IService<ExportTaskProgressDo> {

    /**
     * 创建任务
     *
     * @param submitReq
     */
    ExportTaskProgressDo createTask(SubmitReq submitReq);
}
