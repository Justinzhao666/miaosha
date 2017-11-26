package top.zhaohaoren.miaosha.dao;

import org.apache.ibatis.annotations.Param;
import top.zhaohaoren.miaosha.entity.SuccessKilled;

public interface SuccessKilledDao {
    /**
     * 秒杀成功插入到订单表中
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);


    /**
     * 根据秒杀商品的id查询明细SuccessKilled对象(该对象携带了Seckill秒杀产品对象)
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

}
