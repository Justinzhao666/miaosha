package top.zhaohaoren.miaosha.service;

import top.zhaohaoren.miaosha.dto.Exposer;
import top.zhaohaoren.miaosha.dto.SeckillExecution;
import top.zhaohaoren.miaosha.entity.Seckill;
import top.zhaohaoren.miaosha.exception.RepeatKillException;
import top.zhaohaoren.miaosha.exception.SeckillCloseException;
import top.zhaohaoren.miaosha.exception.SeckillException;

import java.util.List;

/*
*  设计业务接口：
*
*   需要站在《使用者》的角度去设计接口。 做为一个类库提供者的话，写出的接口会非常的冗余。
*   就是尽可能的简便。
*
*
* */


/**
 *  秒杀的业务逻辑接口
 */
public interface SeckillService {

    /**
     * 显示所有的秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 显示单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 在秒杀开始时才输出秒杀的地址，否则输出当前时间和秒杀时间。防止用户非法秒杀。
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作，做reduce操作，其结果可能成功或者失败，所以抛出自定义的异常来用于事务回滚。
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;

    /**
     * 执行秒杀操作,通过存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
}
