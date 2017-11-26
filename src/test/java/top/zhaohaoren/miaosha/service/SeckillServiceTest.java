package top.zhaohaoren.miaosha.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zhaohaoren.miaosha.dto.Exposer;
import top.zhaohaoren.miaosha.dto.SeckillExecution;
import top.zhaohaoren.miaosha.entity.Seckill;
import top.zhaohaoren.miaosha.exception.RepeatKillException;
import top.zhaohaoren.miaosha.exception.SeckillCloseException;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})//配置两个是,是因为service依赖dao
public class SeckillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() throws Exception {
        Seckill seckill = seckillService.getById(1);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1;
        Exposer export = seckillService.exportSeckillUrl(id);
        logger.info("export:" + export);
        //:Exposer{exposed=true, md5='eba6ffd42ce80285629d7d6d124bbf28', seckillId=1, now=0, start=0, end=0}
    }


    //注意测试代码的可重复性质: 可以重复测试依然为pass
    @Test
    public void executeSeckill() throws Exception {
        long id = 1;
        long phoneNumber = 12345678701L;
        String md5 = "eba6ffd42ce80285629d7d6d124bbf28";
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phoneNumber, md5);
            logger.info("seckillExecution" + seckillExecution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }
    }


    @Test              
    public void executeSeckillByProcedure(){
        long seckillId = 1;
        long userPhone = 12311024617L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillByProcedure(seckillId, userPhone, md5);
            logger.info(execution.getStateInfo());
        }
    }

}