package fun.werfamily.starter.lock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2022/2/28 11:49
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class LockTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void lockR() throws Exception {
        RLock lock = redissonClient.getLock("t-lock");
        boolean locked = lock.tryLock(10, 5, TimeUnit.SECONDS);
        if (locked) {
            try {
                System.out.println("lock success.");
            } finally {
                lock.unlock();
            }
        }
    }
}
