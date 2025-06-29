package fun.werfamily.framework.excel.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.werfamily.framework.excel.domain.*;
import fun.werfamily.framework.excel.enums.ExportTaskStatusEnums;
import fun.werfamily.framework.excel.executor.ExportTaskExecutor;
import fun.werfamily.framework.excel.executor.IExportSourceHanlder;
import fun.werfamily.framework.excel.service.IExportTaskProgressService;
import fun.werfamily.framework.excel.util.ExportBeanConvertor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Description: 异步导出任务API
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/11.
 */
@Component
public class ExportTaskApi {

    @Autowired
    private IExportTaskProgressService iExportTaskProgressService;

    @Autowired
    private ExportTaskExecutor executor;

    /**
     * 提交异步导出任务
     *
     * @param submitReq
     * @param iExportSourceHanlder
     */
    public ExportTaskProgressDo submit(SubmitReq submitReq, IExportSourceHanlder iExportSourceHanlder) {
        //创建任务
        ExportTaskProgressDo exportTaskProgressDo = iExportTaskProgressService.createTask(submitReq);
        //触发任务
        executor.execut(exportTaskProgressDo, iExportSourceHanlder);
        return exportTaskProgressDo;
    }

    /**
     * 查询指定任务进度
     *
     * @param taskId
     * @return
     */
    public ExportTaskProgressDTO getTaskProgress(String taskId) {
        QueryWrapper<ExportTaskProgressDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ExportTaskProgressDo::getTaskId, taskId);
        ExportTaskProgressDo exportTaskProgressDo = iExportTaskProgressService.getOne(queryWrapper);
        if(exportTaskProgressDo == null) {
            return null;
        }
        ExportTaskProgressDTO exportTaskProgressDTO = new ExportTaskProgressDTO();
        BeanUtils.copyProperties(exportTaskProgressDo, exportTaskProgressDTO);

        buildExportTaskProgressDTO(exportTaskProgressDTO);
        return exportTaskProgressDTO;
    }

    /**
     * 查询导出任务列表
     *
     * @param queryTaskListReq
     * @return
     */
    public ExportPageResp<ExportTaskProgressDTO> queryTaskList(QueryTaskListReq queryTaskListReq) {
        QueryWrapper<ExportTaskProgressDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ExportTaskProgressDo::getUserId, queryTaskListReq.getUserId())
        .eq(ExportTaskProgressDo::getBusinessId, queryTaskListReq.getBusinessId())
        .eq(ExportTaskProgressDo::getUserType, queryTaskListReq.getUserType())
        .eq(StringUtils.isNotBlank(queryTaskListReq.getExportType()), ExportTaskProgressDo::getExportType, queryTaskListReq.getExportType());
        Page<ExportTaskProgressDo> page = PageHelper.startPage(queryTaskListReq.getPageNum(), queryTaskListReq.getPageSize(), "id desc");
        iExportTaskProgressService.list(queryWrapper);
        List<ExportTaskProgressDTO> list = ExportBeanConvertor.convertorToList(page.getResult(), ExportTaskProgressDTO.class);

        if(CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(task -> {
                buildExportTaskProgressDTO(task);
            });
        }

        return ExportPageResp.success(list, page.getPageNum(), page.getPageSize(), (int)page.getTotal());
    }

    public void buildExportTaskProgressDTO(ExportTaskProgressDTO exportTaskProgressDTO) {
        if(exportTaskProgressDTO == null) {
            return;
        }

        Date expireTime = exportTaskProgressDTO.getExpireTime();
        if(expireTime != null) {
            if(expireTime.before(new Date())) {
                exportTaskProgressDTO.setStatus(ExportTaskStatusEnums.EXPIRE.getVal());
            }
        }

        if(exportTaskProgressDTO.getStatus().intValue() == ExportTaskStatusEnums.SUCCESS.getVal()) {
            exportTaskProgressDTO.setExportProgressBar(100d);
        }else if(exportTaskProgressDTO.getStatus().intValue() == ExportTaskStatusEnums.FAIL.getVal()) {
            exportTaskProgressDTO.setExportProgressBar(0d);
        }else {
            exportTaskProgressDTO.setExportProgressBar(calculateProgressBar(exportTaskProgressDTO.getExportProgress(), exportTaskProgressDTO.getExportTotal()));
        }
    }

    /**
     * 计算百分比
     *
     * @param numerator
     * @param denominator
     * @return
     */
    private Double calculateProgressBar(Integer numerator, Integer denominator) {
        Double n = Double.parseDouble(numerator.toString());
        Double d = Double.parseDouble(denominator.toString());
        Double bar = n/d*100;
        return Math.floor(bar*10)/10;
    }

}
