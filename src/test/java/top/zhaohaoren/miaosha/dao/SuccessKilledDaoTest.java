package top.zhaohaoren.miaosha.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zhaohaoren.miaosha.entity.Seckill;
import top.zhaohaoren.miaosha.entity.SuccessKilled;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() throws Exception {
        long id = 1;
        long phone = 15361056888L;
        System.out.println(successKilledDao.insertSuccessKilled(id,phone));
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long id = 1;
        long phone = 15361056888L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,phone);
        System.out.println(successKilled);
    }

}