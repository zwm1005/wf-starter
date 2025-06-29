package fun.werfamily.framework.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.werfamily.framework.excel.config.ExportTaskConfig;
import fun.werfamily.framework.excel.dao.ExportTaskProgressMapper;
import fun.werfamily.framework.excel.domain.ExportTaskProgressDo;
import fun.werfamily.framework.excel.domain.SubmitReq;
import fun.werfamily.framework.excel.enums.ExportTaskStatusEnums;
import fun.werfamily.framework.excel.service.IExportTaskProgressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 * 导出任务进度 服务实现类
 * </p>
 *
 * @Author Mr.WenMing
 * @since 2022-07-11
 */
@Service
public class ExportTaskProgressServiceImpl extends ServiceImpl<ExportTaskProgressMapper, ExportTaskProgressDo> implements IExportTaskProgressService {

    @Autowired
    private ExportTaskConfig exportTaskConfig;

    @Override
    public ExportTaskProgressDo createTask(SubmitReq submitReq) {
        ExportTaskProgressDo exportTaskProgressDo = new ExportTaskProgressDo();
        BeanUtils.copyProperties(submitReq, exportTaskProgressDo);
        exportTaskProgressDo.setTaskId(UUID.randomUUID().toString().replaceAll("-", ""));
        exportTaskProgressDo.setExportProgress(0);
        exportTaskProgressDo.setExportTotal(0);
        exportTaskProgressDo.setStatus(ExportTaskStatusEnums.ING.getVal());
        exportTaskProgressDo.setCreateBy(submitReq.getUserId());
        exportTaskProgressDo.setExportMaxTotal(submitReq.getExportMaxTotal() == null ? exportTaskConfig.getDefaultExportMaxTotal() : submitReq.getExportMaxTotal());
        exportTaskProgressDo.setExportPageSize(submitReq.getExportPageSize() == null ? exportTaskConfig.getDefaultExportPageSize() : submitReq.getExportPageSize());
        exportTaskProgressDo.setExportIntervalTime(submitReq.getExportIntervalTime() == null ? exportTaskConfig.getDefaultIntervalTime() : submitReq.getExportIntervalTime());
        exportTaskProgressDo.setDataParam(submitReq.getDataParam());
        this.save(exportTaskProgressDo);
        return exportTaskProgressDo;
    }

}
