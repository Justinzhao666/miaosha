--存储过程--
DELIMITER $$  --将；结束转为 $$ 因为存储过程中有很多 ; 这样会异常中断

-- 定义存储过程
-- 参数: in 输入参数; out 输出参数
-- count_count():返回上一级修改类型sql(delete,insert,update)的影响行数
-- row_count: 0 :未修改数据       >0 表示修改的行数          <0:sql错误
CREATE PROCEDURE `seckill`.`execute_seckill`
    (in v_seckill_id bigint, in v_phone bigint,
    in v_kill_time timestamp, out r_result int)
BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION;
    insert ignore into success_killed
        (seckill_id, user_phone, create_time)
        value (v_seckill_id, v_phone, v_kill_time);
    select row_count() into insert_count;
    IF (insert_count = 0) THEN
        ROLLBACK;
        set r_result = -1;
    ELSEIF(insert_count < 0) THEN
        ROLLBACK;
        set r_result = -2;
    ELSE
        UPDATE seckill
            set number = number - 1
            where seckill_id = v_seckill_id
		        and end_time > v_kill_time
		        and start_time < v_kill_time
		        and number > 0;
            select row_count() into insert_count;
        IF(insert_count = 0)   THEN
           ROLLBACK;
           set r_result = 0;
        ELSEIF (insert_count < 0) THEN
           ROLLBACK;
           set r_result = -2;
        ELSE
           COMMIT;
           set r_result = 1;
        END IF;
    END IF;
END;
$$ --表示换行

-- 存储过程定义结束
DELIMITER ;

-- 定义一个变量，用来获取结果，call的结果会返回给r_result
set @r_result = -3;
-- 执行存储过程
call execute_seckill(1,13511024618,now(),@r_result);

--获取结果
select @r_result;

-- 存储过程优化
-- 1.优化的是:事务行级锁持有的时间
-- 2.不要过度依赖存储过程,银行多用，互联网不怎么用
-- 3.简单的逻辑,可以应用存储过程(一般不推荐存储过程,项目逻辑难以维护)
-- 4.QPS:一个秒杀单6000/qps