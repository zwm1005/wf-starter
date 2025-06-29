### 项目配置
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-core-boot</artifactId>
    <version>x.y.z</version>
</dependency>
```

### 应用健康检查配置
```yaml
management:
  endpoint:
    info: # 关闭info（默认开启）
      enabled: false
    health: # 开启health 配置细节展示为所有
      enabled: true
      show-details: always
  endpoints: #根路径
    web:
      base-path: / 
```