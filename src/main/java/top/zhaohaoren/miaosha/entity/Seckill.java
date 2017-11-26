package top.zhaohaoren.miaosha.entity;

import java.util.Date;

public class Seckill {
    private long goodId; //对应数据库里面的good_id，一定要这么写！
    // 约定大于编码，mybatis.xml里面配置了转换会自动将数据库查询结果good_id映射到goodId字段上去。
    private String goodName;
    private int goodNumber;
    private Date startTime;
    private Date endTime;
    private Date createTime;

    public long getGoodId() {
        return goodId;
    }

    public void setGoodId(long goodId) {
        this.goodId = goodId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public int getGoodNumber() {
        return goodNumber;
    }

    public void setGoodNumber(int goodNumber) {
        this.goodNumber = goodNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Seckill{" +
                "goodId=" + goodId +
                ", goodName='" + goodName + '\'' +
                ", goodNumber=" + goodNumber +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                '}';
    }
}
