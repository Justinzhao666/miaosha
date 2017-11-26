package top.zhaohaoren.miaosha.exception;

/**
 * 秒杀业务的相关系异常
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
