### 工程配置
项目引入如下配置
```xml
<dependency>
    <artifactId>wf-starter-timer</artifactId>
    <groupId>fun.werfamily.project</groupId>
    <version>x.y.z</version>
</dependency>
```
Timer基于Redisson队列实现，所以需要依赖redis-starter配置
```yaml
spring:
  redis:
    host: 192.168.3.192
    database: 1
    port: 6379
    password: 123456
```
### 代码示例
```java
// 注入延迟队列管理器，用于提交任务
@Autowired
private DelayJobCommitter delayJobCommitter;

// 新建任务执行器，需实现JobExecutor接口，InParam为任务入参对象
public class MyExecutor implements JobExecutor<DelayJob<InParam>, InParam> {

    @Override
    public void execute(DelayJob<InParam> job) {
        InParam param = job.getJobParams();
        System.out.println("execute task success: " + param.getXXX());
    }
}

// 新建任务入参并提交任务到延迟队列，如下为延迟3秒后执行
InParam inParam = new InParam("xxx", 1L);
delayJobCommitter.commit(new DelayJob<>(MyExecutor.class, inParam), 3L, TimeUnit.SECONDS);

```