package fun.werfamily.starter.timer;

import fun.werfamily.delayqueue.executor.JobExecutor;
import fun.werfamily.delayqueue.job.DelayJob;
import fun.werfamily.delayqueue.service.DelayJobCommitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @Author : Mr.WenMing
 * @create 2022/2/28 16:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class TaskTest {

    @Autowired
    private DelayJobCommitter delayJobCommitter;

    @Test
    public void testTaskCommit() {
        TExecutor.User user = new TExecutor.User("lu", 1L);
        delayJobCommitter.commit(new DelayJob<>(TExecutor.class, user), 3L, TimeUnit.SECONDS);
    }

    private static class TExecutor implements JobExecutor<DelayJob<TExecutor.User>, TExecutor.User> {

        @Override
        public void execute(DelayJob<User> job) {
            User user = job.getJobParams();
            System.out.println("execute task success." + user.getUserName());
        }

        public static class User {
            private String userName;
            private Long id;

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public User(String userName, Long id) {
                this.userName = userName;
                this.id = id;
            }
        }
    }


}
