package top.zhaohaoren.miaosha.exception;

/**
 * 秒杀关闭异常
 *
 * 秒杀时间到了和库存到了 就不能再进行秒杀了。
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
