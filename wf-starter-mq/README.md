### 项目配置
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-starter-mq</artifactId>
    <version>x.y</version>
</dependency>
```
### 参考配置
```yaml
spring:
  rocketmq:
    name-server-address: xxx.xx.xx.xxx:xxx
    producer-group: 应用别名_group
    # 发送超时配置毫秒数, 可选, 默认3000
    send-msg-timeout: 5000
    # 追溯消息具体消费情况的开关，默认打开
    trace-enabled: false
```
### 通过注解的方式启用MQ
```java
//启动类添加此注解启用MQ
@EnableMQConfiguration
public class Application {
    ...
}
```

### Consumer
```java
//TAG禁止使用*
@MQConsumer(topic = "topic", tag = "tag", consumerGroup = "group")
@Slf4j
public class TestMQ extends AbstractMQPushConsumer<NotMr.WenMingParam> {

    @Override
    public boolean process(NotMr.WenMingParam notMr.WenMingParam, Map<String, Object> map) {
        //失败重试返回 false
        return true;
    }
}
```
### Producer
```java
@Autowired
public class TestMQ {
    /**
     * WfDefaultMQProducer也实现了原生同步异步的发送Message的方式
     * Message 消息体序列化的方式 一定要使用  alibaba.fastjson
     */
    @Autowired
    private WfDefaultMQProducer wfDefaultMQProducer;
    public void testMq() {
        //简单同步方式
        wfDefaultMQProducer.syncSend("settle-topic", "TEST_ONE", UUID.randomUUID().toString(), triggerParam);
        //原生异步消息
        Message message = new Message("settle-topic", "TEST_ONE", JSON.toJSONString(triggerParam).getBytes(StandardCharsets.UTF_8));
        wfDefaultMQProducer.asyncSend(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                //发送成功
            }

            @Override
            public void onException(Throwable throwable) {
                //发送异常
            }
        });
    }
}
```