package fun.werfamily.sequence.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * application entry
 *
 * @AuthorMr.WenMing
 */
@SpringBootApplication(scanBasePackages = {"fun.werfamily.sequence"})
public class TestApplication {
    public static void main(String[] args) {
        new SpringApplication(TestApplication.class).run(args);
    }
}