package top.zhaohaoren.miaosha.dto;

/**
 * VO:value object值对象。通常用于业务层之间的数据传递，和PO一样也是仅仅包含数据而已。但应是抽象出的业务对象,可以和表对应,也可以不,这根据业务的需要.个人觉得同DTO(数据传输对象),在web上传递。
 *
 * 疯转controller层的json结果
 */
public class SeckillResult<T> {

    private boolean success;
    private T data;
    private String error;

    //返回结果成功 就返回数据
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    //如果是失败的 那么就要返回错误信息
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
