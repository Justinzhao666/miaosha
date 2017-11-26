package top.zhaohaoren.miaosha.dto;

import top.zhaohaoren.miaosha.entity.SuccessKilled;
import top.zhaohaoren.miaosha.enums.SeckillStatEnum;

/**
 * 该类用于封装秒杀执行后的结果：
 * 秒杀结束后需要向前台返回秒杀是否成功等信息
 */
public class SeckillExecution {


    private long seckillId;

//    private SeckillStatEnum state;  这个对象需要通过json传递到前端去，但是枚举序列化是有问题的.所以这里持有int
    private int state;



    private  String  stateInfo;

    private SuccessKilled successKilled;

    /*
    * 两个构造方法： 用于初始化成功的结果和失败的结果
    * */

    // 当秒杀成功的话，我们使用这个构造方法在service进行构造返回给web
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum,SuccessKilled successKilled) {
        super();
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }
    //当秒杀失败的时候，使用该构造，只需要返回失败状态和失败的原因。
    public SeckillExecution(long seckillId, SeckillStatEnum state) {
        this.seckillId = seckillId;
        this.state = state.getState();
        this.stateInfo = state.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(SeckillStatEnum state) {
        this.state = state.getState();
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}
