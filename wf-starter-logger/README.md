##项目简介
```text
日志组件依赖Slf4j框架包，业务在打印日志时添加@Slf4j类注解即可。
日志框架采用log4j2，生产环境默认使用高性能异步模式输出。注意Sentry相关配置，只有生产环境才会被激活。
控制层等统一切面相关日志请参考wf-starter-util。
```
##工程依赖
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-starter-logger</artifactId>
    <version>x.y.z</version>
</dependency>
```
##示例代码
```java
@Slf4j
@Component
public class IOTClient {
    // 其他业务代码
    public void test() {
        log.info("设备注册到华为云IOT平台, request={}","xxx");
    }
}
```