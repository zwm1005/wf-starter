### 基础原理
```textmate
* +------+----------------------+----------------+-----------+
* | sign |     delta seconds    | worker node id | sequence  |
* +------+----------------------+----------------+-----------+
* 1bit          40bits              17bits         6bits
序列号总计使用64位二进制位来实现，sequence保留6bit可支撑64000/second的并发诉求，17bits的机器码可支持同应用131071次分配(20次每天可持续使用17年)
41bits时间位能在34年内不重复（API默认添加yyyyMMdd前缀）
```
### 项目配置
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-starter-sequence</artifactId>
    <version>x.y</version>
</dependency>
```
如下示例配置，注意项目依赖redis，用来存储machineId，另外sequence.bizCode建议配置为项目名
```yml
com:
  wf:
    sequence:
      bizCode: test-s1  #必填项 建议项目名称简写
spring:
  profiles:
    active: dev
  redis:   #必填项 存储machineId
    database: 1
    host: 192.168.3.192
    password: 123456
    port: 6379
```
### 普通Long类型唯一UID
```java
@Autowired
private CachedUidGenerator cachedUidGenerator;
Long uid = cachedUidGenerator.getBizUID();
```
### 业务String带日期业务标识UID 
```java
@Autowired
private CachedUidGenerator cachedUidGenerator;
//UidServiceEnum业务系统标识枚举，使用自己域的定义
String uid = cachedUidGenerator.getBizUID(UidServiceEnum.OTHER);
```