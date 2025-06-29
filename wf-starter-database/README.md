### 项目配置
```xml
<dependency>
    <artifactId>wf-starter-database</artifactId>
    <groupId>fun.werfamily.project</groupId>
    <version>x.y.z</version>
</dependency>
```
### 数据库参考配置
```yaml
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:mysql://10.10.xx.xx:3306/iot_platform?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&allowMultiQueries=true&tinyInt1isBit=false
    username: xxx
    password: xxxxx
    hikari:
      minimum-idle: 3
      maximum-pool-size: 10
      auto-commit: true
      connection-timeout: 30000
```