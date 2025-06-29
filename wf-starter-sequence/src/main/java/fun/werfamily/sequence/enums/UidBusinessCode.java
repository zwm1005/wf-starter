package fun.werfamily.sequence.enums;


/**
 * uid生成可自带业务编码  范围从0-9
 * @AuthorMr.WenMing
 */
public interface UidBusinessCode {
    /**
     * 业务编码
     *
     * @return 状态码
     */
    Integer code();

    /**
     * 业务
     *
     * @return 提示信息
     */
    String business();

}
