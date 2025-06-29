package fun.werfamily.transaction.framework.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * application entry
 *
 * @AuthorMr.WenMing
 */
@SpringBootApplication(scanBasePackages = {"fun.werfamily.transaction.framework"})
@MapperScan(basePackages = "fun.werfamily.transaction.framework.mapper")
public class TestApplication {
    public static void main(String[] args) {
        new SpringApplication(TestApplication.class).run(args);
    }
}