package top.zhaohaoren.miaosha.exception;


/**
 * 重复秒杀异常：
 *
 * 用户的非法操作，对同一件商品不应该让其重复秒杀。
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
