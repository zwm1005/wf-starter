package fun.werfamily.framework.obs.service;

import fun.werfamily.framework.obs.domain.FileUploadResp;

import java.io.File;
import java.io.InputStream;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
public interface FileUploadService {

    FileUploadResp upload(InputStream inputStream, String key);

    FileUploadResp upload(File file, String key);

    FileUploadResp uploadExpire(InputStream inputStream, String key, int expire);

    FileUploadResp uploadExpire(File file, String key, int expire);

    boolean delete(String key);
}
