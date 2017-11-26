package top.zhaohaoren.miaosha.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zhaohaoren.miaosha.dao.SeckillDao;
import top.zhaohaoren.miaosha.entity.Seckill;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class) /*junit启动时加载spring IOC容器*/
@ContextConfiguration({"classpath:spring/spring-dao.xml"}) /*告诉junit spring的位置*/
public class RedisDaoTest {

    private long id = 1;

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() throws Exception{
        //get and put
        Seckill seckill = redisDao.getSeckill(id);
        if(seckill == null){
            seckill = seckillDao.queryById(id);
            if(seckill != null){
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }

    }
}