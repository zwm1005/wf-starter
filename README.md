# wf-starter

## 项目简介

**`wf-starter`** 是由 [zwm1005](https://github.com/zwm1005) 打造的企业级微服务通用脚手架与中间件组件库，集成了分布式系统开发中高频且易用的最佳实践，适用于企业数字化、SaaS平台、行业解决方案等场景。项目以高扩展性、易集成、可观测性、云原生友好为核心目标，助力团队和个人快速搭建高质量、工程化的微服务应用。

---

## 设计理念

- **模块化解耦**：每个 Starter 独立可用，按需集成，降低耦合度。
- **最佳实践沉淀**：融合作者多年一线业务与架构经验，聚焦工程落地与生产可用。
- **一站式支撑**：覆盖日志、审计、事务、流控、ID、消息、缓存、任务、加解密等微服务常用能力。
- **云原生友好**：天然支持 Spring Cloud、Nacos、RocketMQ、Redis 等主流云原生技术栈。
- **易于二次开发**：丰富的配置项和扩展点，便于自定义和能力增强。
- **代码优雅、注释清晰、文档完善**：每个模块均有独立 README 及详细注释，易读易用，可作团队代码规范与架构参考。

---

## 主要功能模块

| 模块                  | 说明/特色                                   |
|-----------------------|:---------------------------------------------|
| wf-starter-logger     | 统一日志输出，Log4j2异步高性能，Sentry集成异常监控 |
| wf-core-logaudit      | 注解化操作日志审计，支持多租户、用户追踪             |
| wf-starter-process    | 业务流程编排引擎，支持多租户/多渠道差异化处理      |
| wf-starter-sequence   | 分布式唯一ID生成器，支持高并发与业务前缀             |
| wf-starter-sentinel   | 微服务流控熔断，Sentinel集成，支持自定义降级策略      |
| wf-starter-mq         | RocketMQ消息中间件快速集成，Producer/Consumer注解化 |
| wf-starter-transaction| 分布式事务保障，最终一致性，本地消息表模式           |
| wf-core-excel         | 大批量、异步Excel数据导出，导出任务追踪               |
| wf-core-encrypt       | 敏感数据加解密工具，便捷API                     |
| wf-core-util          | 常用工具集，AOP日志、通用组件、增强型工具方法         |
| wf-starter-cache      | Redis/本地多级缓存，Redisson支持                 |
| wf-starter-nacos      | Nacos注册和配置自动化，云原生生态无缝对接            |
| wf-starter-timer      | 分布式延迟任务、定时任务，Redisson队列实现           |
| ...                   | 更多功能详见各模块README                        |

---

## 依赖环境与版本对应关系

| 技术组件       | 版本号                | 用途/说明                             |
|----------------|----------------------|--------------------------------------|
| JDK            | 1.8                  | 项目源码与生产环境推荐                |
| Spring Boot    | 2.3.x ~ 2.7.x        | 主体兼容区间                          |
| Spring Cloud   | Hoxton.SR9           | 推荐，与Spring Boot 2.3.x兼容         |
| Nacos          | 1.4.x                | 注册与配置中心                        |
| RocketMQ       | 4.9.1                | 消息队列                              |
| Redis          | 5.x/6.x              | 分布式缓存、分布式ID、延迟队列         |
| Sentinel       | 1.8.x                | 流控与熔断                            |
| MySQL          | 5.7/8.0              | 关系型数据库                          |
| Sentry         | 6.29.0               | 日志报警、异常追踪                    |
| MyBatis        | 2.1.4                | ORM                                  |
| MyBatis-Plus   | 3.4.3.4              | ORM增强                              |
| PageHelper     | 1.3.1                | 分页插件                              |
| EasyExcel      | 3.1.1                | Excel导出                            |
| Redisson       | 3.16.4               | 分布式锁、队列、缓存                  |
| Druid          | 1.1.21               | 数据源连接池                          |
| Knife4j        | 3.0.2                | Swagger增强API文档                    |
| Fastjson       | 1.2.78                | JSON序列化                            |
| Log4j2         | 2.17.0+              | 日志框架                              |
| Lombok         | 1.18.24              | 代码注解简化                          |

**更多依赖和细节请查阅 [wf-starter-bom/pom.xml](https://github.com/zwm1005/wf-starter/blob/main/wf-starter-bom/pom.xml) 及各模块 pom 文件。**

---

## 典型使用场景

- SaaS平台/行业解决方案：统一技术栈、统一运维、支持多租户与渠道差异。
- 企业内高复用率的基础微服务集成。
- 业务复杂、要求高可观测与自动化治理的分布式系统。
- 需要分布式ID、分布式事务、消息队列、流控等基础能力的系统。
- 工程实践、代码规范、架构教学的范例项目。

---

可以按需引入多个 Starter 实现能力组合。

---

## 🌟 推荐配合 [wf-template](https://github.com/zwm1005/wf-template) 脚手架项目使用

- wf-starter 可与 [wf-template](https://github.com/zwm1005/wf-template) 微服务模板项目无缝配合使用。
- 推荐先通过 wf-template 快速生成标准化微服务项目，然后在新项目的 `pom.xml` 中按需引入 wf-starter 中的各类能力组件，获得数据库、缓存、分布式锁、消息队列等能力的快速集成。
- 这样可以极大提升微服务项目的开发效率和标准化程度，避免重复造轮子。

---

## 📝 例：wf-template + wf-starter 整体开发流程

1. 使用 wf-template 或配套脚本生成新项目骨架
2. 在新业务项目的 pom.xml 中引入所需的 wf-starter 组件
3. 业务代码可直接基于这些能力组件实现，无需关注底层技术细节

---

## 快速入门

1. **引入依赖（以logger为例）：**
   ```xml
   <dependency>
       <groupId>fun.werfamily.project</groupId>
       <artifactId>wf-starter-logger</artifactId>
       <version>3.0.2</version>
   </dependency>
   ```
2. **配置参数**  
   各模块README均有详细配置说明。如：
   ```yaml
   spring:
     redis:
       host: 192.168.3.192
       port: 6379
       password: 123456
   ```
3. **代码集成**  
   按照模块README/示例代码使用注解与API快速集成业务逻辑。

---

## 高级特性与工程亮点

- **注解驱动**：如操作日志、消息消费、分布式ID获取等，极简上手。
- **自动装配**：Starter模式，开箱即用，自动适配Spring Boot生态。
- **可观测性**：日志、审计、异常监控、全链路追踪集成。
- **分布式友好**：ID生成、分布式事务、延迟队列等易用且高性能。
- **丰富的扩展点**：支持自定义SPI、AOP、事件监听，方便二次开发。
- **完善的测试**：多模块单元测试，保障基础能力可靠性。
- **持续集成与发布**：规范的Maven结构，便于团队协作与CI/CD对接。

---

## 目录结构举例

```text
wf-starter/
├── wf-starter-logger/        # 日志组件
├── wf-core-logaudit/         # 操作审计
├── wf-starter-sequence/      # 分布式ID
├── wf-starter-sentinel/      # 流控熔断
├── wf-starter-mq/            # 消息队列
├── wf-starter-transaction/   # 分布式事务
├── wf-core-excel/            # Excel导出
├── wf-core-util/             # 常用工具
├── wf-core-encrypt/          # 加解密
├── wf-starter-nacos/         # Nacos集成
├── wf-starter-cache/         # 缓存组件
├── wf-starter-timer/         # 分布式定时/延迟任务
├── ...                       # 其它模块
```

---

## 贡献指南

- 欢迎 issue、PR，或联系作者交流代码、架构、最佳实践。
- 代码风格统一（Google Java Style），注重注释与文档，适合团队协作。
- 计划逐步补充更多云原生与高阶能力（如服务网格、自动扩缩容等）。
- **更多文档**：详见各子模块 README 与源码注释
- **Star/Watch/Fork 欢迎！**
---

## 📞 联系作者

如有问题或建议，欢迎通过以下方式联系作者或提交 Issue：

- 💬 QQ交流群：`1051233835`
- 📧 邮箱：`chx5508@dingtalk.com`  

---

> 💡 本项目持续迭代中，欢迎 **Star** 和 **PR**！
