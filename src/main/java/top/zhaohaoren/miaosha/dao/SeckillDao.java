package top.zhaohaoren.miaosha.dao;

import org.apache.ibatis.annotations.Param;
import top.zhaohaoren.miaosha.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {

    /**
     * 秒杀减库存数量
     * @param seckillId
     * @param killTime
     * @return 更新库存的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /*
    @Param  mybatis提供的注解，告诉mapper的xml这里的形参名字，如果不加编译器编译的时候优化机制会让改变变量名。
    * */

    /**
     * 根据id查询秒杀的商品信息
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀： 该dao方法调用存储过程函数
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);
}
