### 项目配置
```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-core-util</artifactId>
    <version>x.y.z</version>
</dependency>
```
### log组件参考配置
```yaml
wf:
  starter:
    log:
      # 注解日志开关, true为打开 false为关闭
      openWfLog: true
      # 日志处理实现类 只有Result不是使用脚手架的应用才需要参考DefaultAopLogHandler重新实现 并通过此参数配置对于的Bean
      aopLogHandlerBeanName: DefaultAopLogHandler
```
### log组件通过注解的方式使用
```java
/**
 * 通过方法上使用注解添加日志
 * 
 * value：需要添加的附加日志内容
 * level：日志的输出级别 默认ERROR   ERROR模式仅Controller模式生效，
 * type： 应用层类型 默认Controller 只有type=Controller时  捕捉异常接口统一返回 Result
 * 注：自定义异常 WfValidationException 不会打印ERROR日志
 */
@WfLog(value = "删除数据", type = WfLogType.CONTROLLER, level = WfLogLevel.ERROR)
```

### log组件Controller层日志使用示例
```java
@RestController
@RequestMapping("/api/demo")
@Api(tags = "DEMO")
public class DemoController {
    
    @PostMapping("/add")
    @WfLog
    public Result<DemoDTO> add(@Validated @RequestBody DemoReq demoReq) {
        return Result.success(demoBiz.add(demoReq));
    }
}
```
### log组件非Controller层日志注解使用示例
```java
//此处不需要设置ERROR级别，ERROR级别仅限Controller层使用（没有必要在每一层都去打ERROR）
@WfLog(value = "order", level = WfLogLevel.INFO)
```