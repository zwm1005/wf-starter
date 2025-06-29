package fun.werfamily.sequence.test;

import fun.werfamily.sequence.enums.UidServiceEnum;
import fun.werfamily.sequence.snowflake.service.impl.CachedUidGenerator;
import fun.werfamily.sequence.snowflake.service.impl.UniqueIdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
@TestPropertySource(locations = "classpath:application.properties")
public class BaseTest {

    @Autowired
    private CachedUidGenerator cachedUidGenerator;

    @Autowired
    private UniqueIdGenerator uniqueIdGenerator;

    @Test
    public void getUid() {
        Long uid = cachedUidGenerator.getUID();
        System.out.println(uid);
        System.out.println(cachedUidGenerator.parseUID(uid));
        System.out.println(cachedUidGenerator.getBizUID(UidServiceEnum.ORDER));
    }

    @Test
    public void getUniqueId() {
        for (int i=0;i<5;i++) {
            System.out.println(uniqueIdGenerator.getUID());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
