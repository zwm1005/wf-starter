package fun.werfamily.transaction.framework.enums;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * 远程服务重试策略
 *
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public enum TaskRetryStrategyEnum {

    /**
     * 固定间隔重试，每分钟重试一次
     */
    FIXED_INTERVAL(30, "固定间隔重试") {
        @Override
        public Date calNextExecuteTime(int retriedTimes) {
            return DateUtils.addMinutes(new Date(), retriedTimes);

        }
    },

    /**
     * 重试间隔时间倍数递增， 共重试12次， 1, 2,4,8,16,32,64,128,256,512,1024,2048
     */
    INCREASING_INTERVAL(12, "递增间隔重试") {
        @Override
        public Date calNextExecuteTime(int retriedTimes) {
            return DateUtils.addMinutes(new Date(), (int) Math.pow(2, retriedTimes - 1));
        }
    };

    private String desc;

    private int maxRetryTimes;


    private TaskRetryStrategyEnum(int maxRetryTimes, String desc) {
        this.maxRetryTimes = maxRetryTimes;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 计算下次执行时间
     *
     * @param retriedTimes
     * @return
     */
    public abstract Date calNextExecuteTime(int retriedTimes);


    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

}
