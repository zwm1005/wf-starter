package fun.werfamily.sequence.snowflake.buffer;

import java.util.List;

/**
 * @Author: Mr.WenMing
 * @since: 2021/9/17
 */
@FunctionalInterface
public interface BufferedUidProvider {

    /**
     * 函数实现
     *
     * @param momentInSecond 秒
     * @return 结果
     */
    List<Long> provide(long momentInSecond);
}
