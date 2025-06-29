
### 项目配置
新增引入pom配置如下：

```xml
<dependency>
    <groupId>fun.werfamily.project</groupId>
    <artifactId>wf-core-excel</artifactId>
    <version>x.y.z</version>
</dependency>
```

### 接入方依赖表结构

```sql
CREATE TABLE `export_task_progress` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_id` varchar(32) NOT NULL DEFAULT '' COMMENT '任务ID',
    `task_name` varchar(128) DEFAULT '' COMMENT '任务名称',
    `user_id` varchar(32) NOT NULL DEFAULT '' COMMENT '用户ID',
    `user_type` varchar(16) NOT NULL DEFAULT '' COMMENT '用户类型 1：会员 2.省总 3.市总 3.门店 4.SAAS用户',
    `tenant_id` varchar(32) NOT NULL DEFAULT '' COMMENT '租户ID',
    `business_id` varchar(32) NOT NULL DEFAULT '' COMMENT '业务ID',
    `export_type` varchar(16) NOT NULL DEFAULT 'NOMAL' COMMENT '导出类型',
    `file_url` varchar(256) NOT NULL DEFAULT '' COMMENT '导出附件URL',
    `object_key` varchar(512) DEFAULT NULL COMMENT '云存储key',
    `export_progress` int(8) NOT NULL DEFAULT '0' COMMENT '导出进度',
    `export_total` int(8) NOT NULL DEFAULT '0' COMMENT '导出总数',
    `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态 0 中断 1 进行中 2 已完成 3 已过期',
    `export_max_total` int(8) NOT NULL DEFAULT '0' COMMENT '导出最大总数',
    `export_page_size` int(8) NOT NULL DEFAULT '0' COMMENT '导出分页数量',
    `export_interval_time` int(8) NOT NULL DEFAULT '1000' COMMENT '间隔执行SQL时间',
    `expire_time` timestamp NULL DEFAULT NULL COMMENT '文件过期时间',
    `data_param` text COMMENT '获取数据源参数',
    `remarks` varchar(512) NOT NULL DEFAULT '' COMMENT '备注',
    `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
    `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '修改者',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_task_id` (`task_id`) USING BTREE,
    KEY `idx_user` (`user_id`,`user_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导出任务进度';
```

### 接入mapper配置

```java
@MapperScan({"fun.werfamily.framework.excel.dao"})
public class Application {

    public static void main(String[] args) {
        WfApplication.run(Application.class, args);
    }
}
```

### 代码示例

1.实现导出数据源处理接口

```java
 @Component
public class ExportSourceHanlderImpl implements IExportSourceHanlder<AccountDicExp> {

    @Autowired
    private IAccountDicService iAccountDicService;

    @Override
    public List<AccountDicExp> getSourceData() {
        List<AccountDicDo> lists =  iAccountDicService.list();
        List<AccountDicExp> accountDicExps = BeanConvertor.convertorToList(lists, AccountDicExp.class);
        return accountDicExps;
    }

    @Override
    public String getOrderBy() {
        return " id ";
    }

    @Override
    public Class getSourceDataClass() {
        return AccountDicExp.class;
    }
}
```

2.提交异步导出任务请求
    exportSourceHanlder 为本地数据源获取接口实现

```java
 exportTaskApi.submit(submitReq, exportSourceHanlder)
```

3.查询指定的任务信息

```java
 exportTaskApi.getTaskProgress(taskId)
```

4.查看导出任务列表

```java
 exportTaskApi.queryTaskList(queryTaskListReq)
```