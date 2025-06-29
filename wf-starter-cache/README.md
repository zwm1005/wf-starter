### 工程配置

```xml
<dependency>
    <artifactId>wf-starter-cache</artifactId>
    <groupId>fun.werfamily.project</groupId>
    <version>x.y.z</version>
</dependency>
```

### 本地缓存
本地缓存基于开源caffeine实现，caffeine支持全局定义缓存key的过期策略，工程需配置如下启动参数

```yaml
spring:
  cache:
    type: caffeine
	
wf:
  cache:
    caffeine:
      config:
        duration: xxx     #缓存过期时间 单位秒（s），默认值-1表示无限制
        initCapacity: xx  #全局初始化缓存数量，默认值-1表示无限制
        maximumSize:  xx  #全局最大缓存数量，默认值-1表示无限制
```
业务使用示例如下
```java
@Cacheable(cacheNames = "xxx",key = "#root.args[1] + ':' + #root.args[0].startDate")
public PromoterOrderStatisticDTO queryVariableData(CommonDateParam param, Integer promoterId) {
	// 业务逻辑
}
```

### 远端缓存
远端缓存默认集成spring-redission实现，可灵活定义每个缓存key的过期策略。
工程需配置如下参数，使用方式同本地缓存，序列化默认使用json格式。**注意redis集群缓存强制需要指定过期时间，当ttl和maxIdleTime未初始化或都为0时将永不过期**。
```yaml
spring:
  cache:
    type: redis
  redis:
    host: 192.168.x.xx
    database: 1
    port: 6379
    password: xxxx
    
wf:
  cache:
    redis:
      config:
        t-1:                # @Cacheable注解中的cacheNames字段，即业务自定义的全局缓存唯一名称
          ttl: 5000         # 单位毫秒（ms） 参数确定对象在缓存中的最大生存期。缓存中的所有对象的生存时间到期后，无论请求的频率如何，都将删除它们。
          maxIdleTime: 5000 # 单位毫秒（ms） 两次请求对象之间可以经过的最长时间，如果这段时间没有请求，对象将自动从缓存中删除。 
        xxx:
          ttl: xxx
          maxIdleTime: xxx
```
业务使用示例如下
```java
@Cacheable(cacheNames = "t-1",key = "#root.args[1] + ':' + #root.args[0].startDate")
public PromoterOrderStatisticDTO queryVariableData(CommonDateParam param, Integer promoterId) {
	// 业务逻辑
}
```
在某些特殊业务场景中，需要更高性能的值存储序列化协议（牺牲value易读性）, 此时建议使用protobuf格式，配置如下：

```yaml
wf:
  cache:
     serializer:
       protobuf:
         enabled: true
```

### 多级缓存
迭代中

