## 1.默认超时配置
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
feign:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
  circuitbreaker:
    enabled: true
    timeLimiter:
      timeOutMills: 5000 #默认配置
  client:
    config:
      default:
        logger-level: basic
```

## 2.resilience4j断路器用户自定义默认配置
```properties
#失败率，错误率达到或高于该值则进入open状态
resilience4j.circuitbreaker.configs.default.failureRateThreshold=50

#慢调用阀值，请求执行的时间大于该值时会标记为慢调用
resilience4j.circuitbreaker.configs.default.slowCallDurationThreshold=60s

#慢调用熔断阀值，当慢调用率达到或高于该值时，进入open状态
resilience4j.circuitbreaker.configs.default.slowCallRateThreshold=100

#时间窗口，用于计算失败率
resilience4j.circuitbreaker.configs.default.slidingWindowSize=60

#状态收集器类型COUNT_BASED:根据数量计算(slidingWindowSize为次数) TIME_BASED:根据时间计算(slidingWindowSize为秒数)
resilience4j.circuitbreaker.configs.default.slidingWindowType=TIME_BASED

#计算错误率的最小请求数，不足最小调用次数不会触发任何变化
resilience4j.circuitbreaker.configs.default.minimumNumberOfCalls=10

#是否自动进入halfOpen状态，默认false-一定时间后进入half-open，ture-需要通过接口执行。
resilience4j.circuitbreaker.configs.default.automaticTransitionFromOpenToHalfOpenEnabled=true

#进入halfOpen状态时，可以被调用次数，计算这些请求的失败率，低于设置的失败率，则断路器变为close状态，否则断路器变为open状态
resilience4j.circuitbreaker.configs.default.permittedNumberOfCallsInHalfOpenState=2

#断路器从OPEN状态变成HALF_OPEN状态需要的等待时间，即熔断多久后开始尝试访问被熔断的服务。
resilience4j.circuitbreaker.configs.default.waitDurationInOpenState=30s

#被计为失败的异常集合，默认情况下所有异常都为失败。
resilience4j.circuitbreaker.configs.default.recordExceptions[0]=java.lang.Exception

#不会被计为失败的异常集合，优先级高于recordExceptions。
resilience4j.circuitbreaker.configs.default.ignoreExceptions[0]=java.lang.IllegalStateException
```
