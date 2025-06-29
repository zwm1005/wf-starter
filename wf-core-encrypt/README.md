### 项目配置
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-core-encrypt</artifactId>
    <version>x.y.z</version>
</dependency>
```

### 使用示例
```java
@Autowired
private WfEncryptApi wfEncryptApi;

//加密
wfEncryptApi.encrypt("哈哈", "123");
//解密
wfEncryptApi.decrypt(ciphertext, "123")
```
