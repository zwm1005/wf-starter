package fun.werfamily.sequence.snowflake.service;

import fun.werfamily.sequence.enums.UidServiceEnum;
import fun.werfamily.sequence.exception.UniqueIdGeneratorException;

/**
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public interface UidGenerator {

    /**
     * get uuid from cache uid generator
     *
     * @return long
     * @throws UniqueIdGeneratorException
     */
    long getUID() throws UniqueIdGeneratorException;

    /**
     * 默认添加前缀及领域编码
     *
     * @param uidServiceEnum 领域定义
     * @return 单号
     */
    String getBizUID(UidServiceEnum uidServiceEnum);
}
