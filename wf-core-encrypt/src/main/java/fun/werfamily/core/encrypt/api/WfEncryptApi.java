package fun.werfamily.core.encrypt.api;

import fun.werfamily.core.encrypt.common.AES;
import fun.werfamily.core.encrypt.common.ConvertUtils;
import fun.werfamily.core.encrypt.config.EncryptConfiguration;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/6/28.
 */
@Component
public class WfEncryptApi {

    @Autowired
    private EncryptConfiguration encryptConfiguration;

    /**
     * 加密
     *
     * @param content
     * @param salt
     * @return
     */
    public String encrypt(String content, String salt) {
        String aesKey = createAeskey(salt);
        return AES.encryptToBase64(ConvertUtils.stringToHexString(content), aesKey);
    }

    /**
     * 解密
     *
     * @param ciphertext
     * @param salt
     * @return
     */
    public String decrypt(String ciphertext, String salt) {
        String aesKey = createAeskey(salt);
        return ConvertUtils.hexStringToString(AES.decryptFromBase64(ciphertext, aesKey));
    }



    public String createAeskey(String key) {
        if(StringUtils.isBlank(encryptConfiguration.privateKey)) {
            throw new IllegalArgumentException("缺少配置 fun.werfamily.encrypt.privateKey！");
        }

        key = key + encryptConfiguration.privateKey;
        return DigestUtils.md5Hex(key).substring(0, 16);
    }


    public static void main(String[] args) {
        String ciphertext = new WfEncryptApi().encrypt("哈哈", "123");
        System.out.println(ciphertext);
        System.out.println(new WfEncryptApi().decrypt(ciphertext, "123"));

    }


}
