## 项目说明
```text
https://docs.sentry.io
Sentry是一个C/S架构，我们需要在自己应用中集成Sentry的SDK才能在应用发生错误是将错误信息发送给Sentry服务端，根据语言和框架的不同，我们可以选择自动或自定义设置特殊的错误类型报告给Sentry服务端。
客户端默认集成log4j2适配器，生产环境在打印log.error时将自动触发上报事件机制。
```

## 接入指南
修改项目POM引入配置如下：
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-starter-sentry</artifactId>
    <version>x.y.z</version>
</dependency>
```
服务端新增配置：
```yaml
sentry:
  enable: true
  dsn: https://xxx@sentry.werfamily.fun/xxx #sentry控制台获取
  stacktrace:
    app:
      packages: fun.werfamily.xxx #项目根目录
  servername: xxx
  mdcTags: traceId
  environment: prod
```