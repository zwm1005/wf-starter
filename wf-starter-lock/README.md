### 工程配置
```xml
<dependency>
    <artifactId>wf-starter-lock</artifactId>
    <groupId>fun.werfamily.project</groupId>
    <version>x.y.z</version>
</dependency>
```

### Redis连接配置
如下配置为single模式的配置，注意非cluster
```yaml
spring:
  redis:
    host: 192.168.3.192
    database: 1
    port: 6379
    password: 123456
```

### 示例代码
```java
public class ServiceA {

    /**
     * 注意API接入方式三选一
     */
    private void methodA(String[]... args) {
        RLock lock = redissonClient.getLock("myLock");
        
        // 方式一 不建议使用的锁方式，默认效期30秒
        lock.lock();

        // 方式二 尝试获取锁，10秒自动超时释放
        lock.lock(10, TimeUnit.SECONDS);

        // 方式三 推荐 -尝试获取锁（等待时长为5秒），10秒后自动释放锁
        boolean res = lock.tryLock(5, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                // 业务逻辑.....
            } finally {
                // finally锁释放为必要动作，避免长时间锁定时长
                lock.unlock();
            }
        }
    }
}

```