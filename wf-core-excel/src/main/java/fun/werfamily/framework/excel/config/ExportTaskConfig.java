package fun.werfamily.framework.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
@Configuration
@ConfigurationProperties("wf.core.export")
@Data
public class ExportTaskConfig {

    /**
     * 本地临时导出目录
     * System.getProperty("java.io.tmpdir")
     */
    private String filePath = System.getProperty("java.io.tmpdir");

    /**
     * 云端存储文件过期时间（天）
     */
    private Integer fileExpire = 7;
    /**
     * 默认最大导出条数
     */
    private Integer defaultExportMaxTotal = 1000000;
    /**
     * 默认每次分页查询数据量
     */
    private Integer defaultExportPageSize = 1000;
    /**
     * 默认分页查询间隔时间（ms）
     */
    private Integer defaultIntervalTime = 1000;
    /**
     * 紧急处理间隔时间
     */
    private Integer systemIntervalTime = 0;

}
