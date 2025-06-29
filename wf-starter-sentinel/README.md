### 微服务流量控制与监控

随着微服务的流行，服务和服务之间的稳定性变得越来越重要。Sentinel 以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性。
### 资源
所有可访问的被保护对象都称作资源，比如一个/xx/xxRestful端点，或者一个Service业务类。

### 接入指南
修改项目POM引入配置如下：
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-starter-sentinel</artifactId>
    <version>x.y.z</version>
</dependency>
```
### 常用配置及说明
1.限流降级触发时提示语配置
```yaml
wf:
  sentinel:
    blockedMessage: "trade center busy"
```

2.spring-web 自动注册 restful 资源端点，已经默认开启
```yaml
spring:
  cloud:
    sentinel:
      enabled: true
```

3.若 blockHandler 和 fallback 都进行了配置，则被限流降级而抛出 BlockException 时只会进入 blockHandler 处理逻辑。
若未配置 blockHandler、fallback，则被限流降级时会将 BlockException 直接抛出（若方法本身未定义 throws BlockException 则会被 JVM 包装一层 UndeclaredThrowableException）。

### 自定义资源代码示例
适用于队列消费等非HTTP请求限流场景
```java
@Service
public class TestServiceB {

    // 原函数 作用于public方法(AOP)
    @SentinelResource(value = "test-resource-endpoint", fallback = "fallback", blockHandler = "blockHandler")
    public String Service(String inText) {
    	throw new RuntimeException("运行时业务异常");
    }

    // Fallback 限流时触发，函数签名与原函数一致或加一个 Throwable 类型的参数. 同blockHandler一起配置时优先调用fallback
    public String fallback(String inText, Throwable ex) {
    	System.out.println("fallback output =>" + ex.getLocalizedMessage());
    	return inText;
    }

    // Block 熔断降级时触发，函数签名与原函数一致或加一个 BlockException类型的参数.
    public String blockHandler(String inText, BlockException ex) {
    	System.out.println("blockHandler output =>" + ex.getRule());
    	return inText;
    }

}
```
