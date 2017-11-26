package top.zhaohaoren.miaosha.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.zhaohaoren.miaosha.dto.Exposer;
import top.zhaohaoren.miaosha.dto.SeckillExecution;
import top.zhaohaoren.miaosha.dto.SeckillResult;
import top.zhaohaoren.miaosha.entity.Seckill;
import top.zhaohaoren.miaosha.enums.SeckillStatEnum;
import top.zhaohaoren.miaosha.exception.RepeatKillException;
import top.zhaohaoren.miaosha.exception.SeckillCloseException;
import top.zhaohaoren.miaosha.service.SeckillService;

import java.util.Date;
import java.util.List;

@Controller  //这些注解的目的就是将这些controller放到容器中去.  MVC - 的controller 控制层 职责:接受参数,依据请求做一些验证和判断做跳转的控制
@RequestMapping("/seckill") //url: /模块/资源/{id}/细分
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired // 注入属性: 这里和你在xml配置bean一样,如果你想要
    private SeckillService seckillService;

    //1 : 获取秒杀的列表页
    // 配置二级的url , 和上面类定义的url进行拼接, method,就是请求的方式,不是该请求方式的请求都会被驳回.
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        /*
        * jsp[页面] + model[数据] = ModeAndView
        * */
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
        // 这里返回字符串list 会和spring-mvc中配置的前缀和后缀组成这个访问路径: /web-inf/jsp/"list".jsp
    }

    //2 : 详情页
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckilledId, Model model) {
        if (seckilledId == null) { //如果获取详情的时候没有给id的时候 重定向到list页面
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckilledId);
        if (seckill == null) { //如果用户请求给的id不对 我们也内部转发到list
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        //
        return "detail";
    }

    //下面写需要ajax返回的json数据给前台的方法

    //输出我们秒杀地址的接口
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"}) //指定一下content-type
    @ResponseBody //会将该函数返回类型转为json数据格式
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckllId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckllId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            // service调用异常就返回 失败的json结果
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    // 执行秒杀操作的时候执行的url
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) { //获取cookie中的电话,require设置false让该参数不是必须,判null逻辑在代码中处理,不由spring来处理.
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e1) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (SeckillCloseException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
    }

    // 获取系统时间的方法
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
}
