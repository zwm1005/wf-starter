package fun.werfamily.framework.obs.service;

import com.obs.services.ObsClient;
import com.obs.services.model.*;
import fun.werfamily.framework.obs.config.ObsConfig;
import fun.werfamily.framework.obs.domain.FileUploadResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
@Component
@Slf4j
public class ObsServiceImpl implements FileUploadService{

    @Autowired
    private ObsConfig obsConfig;

    private ObsClient getObsClient() {
        return new ObsClient(obsConfig.getAk(), obsConfig.getSk(), obsConfig.getEndPoint());
    }

    private FileUploadResp buildResp(PutObjectResult putObjectResult) {
        log.info("obs uplod  {} ", putObjectResult.toString());
        FileUploadResp fileUploadResp = new FileUploadResp();
        fileUploadResp.setUrl(putObjectResult.getObjectUrl());
        fileUploadResp.setObjectKey(putObjectResult.getObjectKey());
        return fileUploadResp;
    }

    private String getObjectKey(String key) {
        return  key + "_" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 文件流上传
     *
     * @param inputStream
     * @param key
     * @return
     */
    @Override
    public FileUploadResp upload(InputStream inputStream, String key) {
        PutObjectResult putObjectResult = getObsClient().putObject(obsConfig.getBucketName(), getObjectKey(key), inputStream);
        return buildResp(putObjectResult);
    }

    /**
     * 文件上传
     *
     * @param file
     * @param key
     * @return
     */
    @Override
    public FileUploadResp upload(File file, String key) {
        PutObjectResult putObjectResult = getObsClient().putObject(obsConfig.getBucketName(), getObjectKey(key), file);
        return buildResp(putObjectResult);
    }

    /**
     * 带过期时间的文件流上传
     *
     * @param inputStream
     * @param key
     * @param expire 天
     * @return
     */
    @Override
    public FileUploadResp uploadExpire(InputStream inputStream, String key, int expire) {
        PutObjectRequest request = new PutObjectRequest(obsConfig.getBucketName(), getObjectKey(key), inputStream);
        request.setExpires(expire);
        PutObjectResult putObjectResult = getObsClient().putObject(request);
        return buildResp(putObjectResult);
    }

    /**
     * 带过期时间的文件上传
     *
     * @param file
     * @param key
     * @param expire 天
     * @return
     */
    @Override
    public FileUploadResp uploadExpire(File file, String key, int expire) {
        PutObjectRequest request = new PutObjectRequest(obsConfig.getBucketName(), getObjectKey(key), file);
        request.setExpires(expire);
        PutObjectResult putObjectResult = getObsClient().putObject(request);
        return buildResp(putObjectResult);
    }

    @Override
    public boolean delete(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        DeleteObjectResult result = getObsClient().deleteObject(obsConfig.getBucketName(), key);
        return result.isDeleteMarker();
    }


}
