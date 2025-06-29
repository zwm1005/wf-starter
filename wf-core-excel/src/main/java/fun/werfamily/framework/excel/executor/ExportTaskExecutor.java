package fun.werfamily.framework.excel.executor;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.werfamily.framework.excel.config.ExportTaskConfig;
import fun.werfamily.framework.excel.domain.ExportTaskProgressDo;
import fun.werfamily.framework.excel.domain.PageCache;
import fun.werfamily.framework.excel.enums.ExportTaskStatusEnums;
import fun.werfamily.framework.excel.service.IExportTaskProgressService;
import fun.werfamily.framework.obs.api.FileUploadApi;
import fun.werfamily.framework.obs.domain.FileUploadResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/11.
 */
@Component
@Slf4j
public class ExportTaskExecutor {

    @Autowired
    private IExportTaskProgressService iExportTaskProgressService;

    @Autowired
    private FileUploadApi fileUploadApi;

    @Autowired
    private ExportTaskConfig exportTaskConfig;


    /**
     * 执行导出
     *
     * @param exportTaskProgressDo
     * @param iExportSourceHanlder
     */
    @Async("exprotTaskExecutor")
    public void execut(ExportTaskProgressDo exportTaskProgressDo, IExportSourceHanlder iExportSourceHanlder) {
        try {
            //导出到本地
            String localFileName = exportLoacl(iExportSourceHanlder, exportTaskProgressDo);
            //上传到文件存储
            FileUploadResp fileUploadResp = fileUpload(localFileName, exportTaskProgressDo.getTaskId());
            //更新状态
            UpdateWrapper<ExportTaskProgressDo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(ExportTaskProgressDo::getTaskId, exportTaskProgressDo.getTaskId())
                    .set(ExportTaskProgressDo::getFileUrl, fileUploadResp.getUrl())
                    .set(ExportTaskProgressDo::getObjectKey, fileUploadResp.getObjectKey())
                    .set(ExportTaskProgressDo::getStatus, ExportTaskStatusEnums.SUCCESS.getVal())
                    .set(ExportTaskProgressDo::getExpireTime, getExpireTime());
            iExportTaskProgressService.update(updateWrapper);
        }catch (Exception e) {
            log.error("执行导出失败 exportTaskProgressDo = {} ", exportTaskProgressDo.getTaskId(), e);
            UpdateWrapper<ExportTaskProgressDo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(ExportTaskProgressDo::getTaskId, exportTaskProgressDo.getTaskId())
                    .set(ExportTaskProgressDo::getStatus, ExportTaskStatusEnums.FAIL.getVal());
            iExportTaskProgressService.update(updateWrapper);
        }

    }

    /**
     * 创建Excel写入器
     *
     * @param fileName
     * @param dataSourceClass
     * @return
     */
    public ExcelWriter createExcelWriter(String fileName, Class dataSourceClass) {
        return EasyExcel.write(fileName, dataSourceClass).registerConverter(new LongStringConverter()).build();
    }


    /**
     * 本地导出EXCEL
     *
     * @param iExportSourceHanlder
     * @return
     * @throws ClassNotFoundException
     */
    public String exportLoacl(IExportSourceHanlder iExportSourceHanlder, ExportTaskProgressDo exportTaskProgressDo) {
        String fileName = createFileName(exportTaskProgressDo);
        Class dataSourceClass = getSourceDataClass(iExportSourceHanlder);

        try (ExcelWriter excelWriter = createExcelWriter(fileName, dataSourceClass)) {
            WriteSheet writeSheet = EasyExcel.writerSheet(exportTaskProgressDo.getTaskName()).build();
            PageCache pageCache = new PageCache(1, 0);
            for (int i = 1; i <= pageCache.getEndPageIndex(); i++) {
                if(pageCache.getExportCount() >= exportTaskProgressDo.getExportMaxTotal()) {
                    break;
                }
                doWrite(excelWriter, writeSheet, exportTaskProgressDo, iExportSourceHanlder, i, pageCache);
                Integer exportIntervalTime = exportTaskProgressDo.getExportIntervalTime();
                if(exportTaskConfig.getSystemIntervalTime() > 0) {
                    //如果系统配置了通用间隔时间，那么以系统配置的优先级高为主，通常此参数用于解决突发问题
                    exportIntervalTime = exportTaskConfig.getSystemIntervalTime();
                }
                if(exportIntervalTime != null && exportIntervalTime.intValue() > 0) {
                    try {
                        Thread.sleep(exportIntervalTime);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }
        return fileName;
    }

    /**
     * 写入EXCEL
     *
     * @param excelWriter
     * @param writeSheet
     * @param exportTaskProgressDo
     * @param iExportSourceHanlder
     * @param i
     * @param pageCache
     */
    public void doWrite(ExcelWriter excelWriter, WriteSheet writeSheet, ExportTaskProgressDo exportTaskProgressDo
        , IExportSourceHanlder iExportSourceHanlder, int i, PageCache pageCache) {
        Integer pageSize = exportTaskProgressDo.getExportPageSize();
        String orderBy = iExportSourceHanlder.getOrderBy();
        Page page = null;
        if(StringUtils.isNotBlank(orderBy)) {
            page = PageHelper.startPage(i, pageSize, orderBy);
        }else {
            page = PageHelper.startPage(i, pageSize);
        }
        List data = iExportSourceHanlder.getSourceData(exportTaskProgressDo.getDataParam());

        int total = (int) page.getTotal();
        Integer endPageIndex = total%pageSize == 0 ? total/pageSize : total/pageSize+1;

        pageCache.setEndPageIndex(endPageIndex);

        List formatSourceData = iExportSourceHanlder.buildSourceData(data, exportTaskProgressDo.getDataParam());

        if(CollectionUtils.isNotEmpty(formatSourceData)) {
            excelWriter.write(formatSourceData, writeSheet);
            pageCache.setExportCount(pageCache.getExportCount() + formatSourceData.size());
            UpdateWrapper<ExportTaskProgressDo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(ExportTaskProgressDo::getTaskId, exportTaskProgressDo.getTaskId())
                    .set(ExportTaskProgressDo::getExportProgress, pageCache.getExportCount())
                    .set(ExportTaskProgressDo::getExportTotal, total);
            iExportTaskProgressService.update(updateWrapper);
        }
    }

    public String createFileName(ExportTaskProgressDo exportTaskProgressDo) {
        String fileName = exportTaskConfig.getFilePath() + "exportTask" + File.separator
                + exportTaskProgressDo.getTaskId() + "_" + exportTaskProgressDo.getTaskId() + ".xlsx";
        log.info("create filename:{} ", fileName);
        File file = new File(exportTaskConfig.getFilePath()+ "exportTask");
        if(!file.exists()) {
            file.mkdirs();
        }
        return fileName;
    }

    /**
     * 获取业务data class
     *
     * @param iExportSourceHanlder
     * @return
     * @throws ClassNotFoundException
     */
    private Class getSourceDataClass(IExportSourceHanlder iExportSourceHanlder) {
        return iExportSourceHanlder.getSourceDataClass();
    }

    /**
     * 过期时间
     * @return
     */
    private Date getExpireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, exportTaskConfig.getFileExpire());
        return calendar.getTime();
    }

    /**
     * 上传附件
     * @param fileName
     * @return
     */
    private FileUploadResp fileUpload(String fileName, String key) {
        FileUploadResp fileUploadResp = null;
        try {
            fileUploadResp = fileUploadApi.uploadExpire(new File(fileName), key, exportTaskConfig.getFileExpire());
        }finally {
            try {
                Files.delete(Paths.get(fileName));
            } catch (IOException e) {
                log.error("删除文件失败 {} ", fileName, e);
            }
        }
        return fileUploadResp;
    }




}
