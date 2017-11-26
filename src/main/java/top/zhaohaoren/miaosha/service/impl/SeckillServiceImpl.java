package top.zhaohaoren.miaosha.service.impl;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import top.zhaohaoren.miaosha.dao.SeckillDao;
import top.zhaohaoren.miaosha.dao.SuccessKilledDao;
import top.zhaohaoren.miaosha.dao.cache.RedisDao;
import top.zhaohaoren.miaosha.dto.Exposer;
import top.zhaohaoren.miaosha.dto.SeckillExecution;
import top.zhaohaoren.miaosha.entity.Seckill;
import top.zhaohaoren.miaosha.entity.SuccessKilled;
import top.zhaohaoren.miaosha.enums.SeckillStatEnum;
import top.zhaohaoren.miaosha.exception.RepeatKillException;
import top.zhaohaoren.miaosha.exception.SeckillCloseException;
import top.zhaohaoren.miaosha.exception.SeckillException;
import top.zhaohaoren.miaosha.service.SeckillService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类是业务的实现类， web传递来的数据都是在这里处理并 去调用dao持久化到数据库的。
 */
/*
@Component 代表所有的组件,当你不知道该类属于dao/service/controller的时候使用该注解
@Service 知道了是service的
@Repository dao的
@Controller controller的*/
@Service //相当于配置了bean
public class SeckillServiceImpl implements SeckillService {

    // logger类 使用的sf4j的log系统
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 需要去调用dao，所以添加引用
    @Autowired //注入依赖, 配置了就会从IOC容器中查找该实例 然后注入到该引用上去.
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;

    //混淆字串
    private final String slat = "shsdssljdd'l.";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /*
    *  获取秒杀商品的信息
    *  1. 正在秒杀
    *  2. 秒杀未开始
    * */
    public Exposer exportSeckillUrl(long seckillId) {
        // 优化点： 缓存优化， 这里面暴露接口的这些东西在数据库一般是不变的， 每个人去获取一个id去查询一下数据库会很耗性能！
        // 将其加入redis，先从redis中获取。’

        Seckill seckill = redisDao.getSeckill(seckillId); //先从redis中获取对象
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                //商品不存在
                return new Exposer(false, seckillId);
            }else{
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            //秒杀时间已经过了
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        // 产品正在秒杀
        String amd5 = getMD5(seckillId);
        return new Exposer(true, amd5, seckillId);
    }

    /**
     * 执行秒杀的方法： 用户点击秒杀操作！
     * 1. 获取商品的id，md5加密后的
     * 2. 进行减库存操作
     * 3. 进行减库存的返回情况
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
        /*使用注解方式控制事务的优点:
    * 1. 开发团队达成一致的约定
    * 2. 保证事务方法的执行时间尽可能短,不要穿插其他的网络操作:rpc/http请求,防止阻塞. 事务的方法尽量就只有数据库操作流程
    * 3. 不是所有的方法都需要事务,所以不适用aop方式. 比如 只有一条sql语句的操作,以及只有读操作的不需要事务控制!
    * */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        //判断用户秒杀的id是否准确,防止用户改了id
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("秒杀的数据被用户修改了");
        }
        try {
            //调整顺序：：：：：：先插入 然后进行update减库存

            // 减库存成功，插入成功秒杀表
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            // 没有插入成功： 因为联合主键冲突，说明重复秒杀
            if (insertCount <= 0) {
                throw new RepeatKillException("重复秒杀");
            } else {
                //执行减库存+ 插入购买记录
                int updateCount = seckillDao.reduceNumber(seckillId, new Date());
                if (updateCount <= 0) {
                    // 没有减成功，代表秒杀结束了,sql中的判断逻辑：如果没更新说明不可以秒杀了
                    throw new SeckillCloseException("秒杀结束");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS);
                }

            }



        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {//处理已知异常之外的那些异常

            logger.error(e.getMessage(), e);
            throw new SeckillException("秒杀系统出错" + e.getMessage());
        }

    }

    //存储过程实现 秒杀
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId,SeckillStatEnum.DATE_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程，执行完成后 result被赋值
        try {
            seckillDao.killByProcedure(map);
            // 获取result的值, 如果没有 返回-2
            int result = MapUtils.getInteger(map,"result",-2);
            if (result ==1){ //秒杀成功
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,sk);
            }else {
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));//依据result来判断是何种状态
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }

    /* 对商品的id进行加密
    *
    * MD5作用：
    * 非法者可能伪造url来进行快速秒杀操作，我们按照比对md5的方式判断秒杀是否放行！
    * 我们定义的slat 客户段不知道，所以这个md5码他不会知道。
    * */
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String amd5 = DigestUtils.md5DigestAsHex(base.getBytes());
        System.out.print("------------------- -------------------------------------");
        return amd5;
    }
}
