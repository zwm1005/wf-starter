package fun.werfamily.framework.obs.api;

import fun.werfamily.framework.obs.domain.FileUploadResp;
import fun.werfamily.framework.obs.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
@Component
public class FileUploadApi {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 文件流上传
     *
     * @param inputStream
     * @param key
     * @return
     */
    public FileUploadResp upload(InputStream inputStream, String key) {
        return fileUploadService.upload(inputStream, key);
    }

    /**
     * 文件上传
     *
     * @param file
     * @param key
     * @return
     */
    public FileUploadResp upload(File file, String key) {
        return fileUploadService.upload(file, key);
    }

    /**
     * 带过期时间的文件流上传
     *
     * @param inputStream
     * @param key
     * @param expire 天
     * @return
     */
    public FileUploadResp uploadExpire(InputStream inputStream, String key, int expire) {
        return fileUploadService.uploadExpire(inputStream, key, expire);
    }

    /**
     * 带过期时间的文件上传
     *
     * @param file
     * @param key
     * @param expire 天
     * @return
     */
    public FileUploadResp uploadExpire(File file, String key, int expire) {
        return fileUploadService.uploadExpire(file, key, expire);
    }

    /**
     * 删除文件
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        return fileUploadService.delete(key);
    }

}
