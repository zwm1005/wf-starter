package fun.werfamily.base.response;

/**
 * Description: 状态码定义
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2021/12/8.
 */
public interface IResponseCode {
    /**
     * 状态码
     *
     * @return 状态码
     */
    int code();

    /**
     * 友好提示信息
     *
     * @return 提示信息
     */
    String friendlyMsg();

    /**
     * 详细信息，通常用于日志输出
     *
     * @return 日志输出
     */
    String logMsg();
}
