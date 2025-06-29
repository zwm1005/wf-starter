package fun.werfamily.core.encrypt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/06/29.
 */
@Configuration
@ConfigurationProperties(prefix = "fun.werfamily.encrypt")
@Data
public class EncryptConfiguration {

    public String privateKey = "A3R8!dka3@9381DKla#0a1LDIQnA2";

}
