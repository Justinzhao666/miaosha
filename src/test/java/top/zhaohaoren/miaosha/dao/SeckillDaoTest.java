package top.zhaohaoren.miaosha.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zhaohaoren.miaosha.entity.Seckill;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/*
* 使用Junit
* 先要整合junit
* */
@RunWith(SpringJUnit4ClassRunner.class) /*junit启动时加载spring IOC容器*/
@ContextConfiguration({"classpath:spring/spring-dao.xml"}) /*告诉junit spring的位置*/
public class SeckillDaoTest {

    //注入Dao实现类的依赖,resource会从IOC容器中去查找实现类
    @Resource
    private SeckillDao seckillDao;


    @Test
    public void reduceNumber() throws Exception {
        long id =1;
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(id,killTime);
        System.out.println("执行结果："+updateCount);
    }

    @Test
    public void queryById() throws Exception {
        long id = 1;
        // 这里报错，数据库里面是从1开始的不是从1000
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getGoodName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {
        List<Seckill> seckills = seckillDao.queryAll(1,10);
        for (Seckill seckill: seckills){
            System.out.println(seckill);
        }
    }
}