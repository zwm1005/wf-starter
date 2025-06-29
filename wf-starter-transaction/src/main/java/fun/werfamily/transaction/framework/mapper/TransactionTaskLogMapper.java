package fun.werfamily.transaction.framework.mapper;

import fun.werfamily.transaction.framework.dao.TransactionTaskLogDO;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * transaction operate mapper
 *
 * @AuthorMr.WenMing
 * @AuthorMr.WenMing
 */
public interface TransactionTaskLogMapper {

    /**
     * save with db server time create time = now(), update = now()
     *
     * @param taskDo taskDo
     * @return id
     */
    int save(TransactionTaskLogDO taskDo);

    /**
     * spec create time and update save
     * use for test unit data generate
     *
     * @param taskDo taskDo
     * @return id
     */
    int saveCustomTime(TransactionTaskLogDO taskDo);

    /**
     * update with db server time update = now()
     *
     * @param taskDo task
     */
    void update(TransactionTaskLogDO taskDo);

    /**
     * query task with spec task id
     *
     * @param taskId   task id
     * @param taskType task type
     * @return exist return task or null if not found
     */
    TransactionTaskLogDO selectByTaskId(@Param("taskId") String taskId, @Param("taskType") String taskType);

    /**
     * 批量更新事务日志状态
     *
     * @param idList       id列表
     * @param targetStatus targetStatus
     * @param sourceStatus sourceStatus
     * @return
     */
    int batchUpdateStatus(@Param("idList") List<Long> idList, @Param("targetStatus") String targetStatus, @Param("sourceStatus") String sourceStatus);


    /**
     * 更新冲正状态
     *
     * @param transactionLog 日志表记录
     * @return
     */
    int updateReversiStatus(TransactionTaskLogDO transactionLog);


    /**
     * 获取努力确保型待重试任务列表
     *
     * @param limitCount 一次取多少量
     * @param hour       往前推多长小时
     * @return 任务列表
     */
    List<TransactionTaskLogDO> selectRetryTaskForInsure(@Param("limitCount") int limitCount, @Param("hour") int hour);


    /**
     * 获取努力确保型长期处理中任务列表
     *
     * @param limitCount 一次取多少量
     * @param hour       往前推多长小时
     * @return 任务列表
     */
    List<TransactionTaskLogDO> selectLongtimeProcessingTaskForInsure(@Param("limitCount") int limitCount, @Param("hour") int hour);


    /**
     * 获取异常冲正型待重试任务列表
     *
     * @param limitCount 一次取多少量
     * @param hour       hour
     * @return 任务列表
     */
    List<TransactionTaskLogDO> selectLongtimeProcessingTaskForReversal(@Param("limitCount") int limitCount, @Param("hour") int hour);

    /**
     * 获取异常冲正型待重试任务列表
     *
     * @param limitCount limitCount
     * @param hour       hour
     * @return
     */
    List<TransactionTaskLogDO> selectRetryTaskTaskForReversal(@Param("limitCount") int limitCount, @Param("hour") int hour);

    /**
     * 删除记录
     *
     * @param expireTime expireTime
     * @param taskStatus taskStatus
     * @return
     */
    int delete(@Param("expireTime") Timestamp expireTime, @Param("taskStatus") List<String> taskStatus);

    /**
     * 根据主键删除
     *
     * @param id id
     * @return
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除
     *
     * @param idList idList
     * @return
     */
    int batchDelete(@Param("idList") List<Long> idList);
}
