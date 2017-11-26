package top.zhaohaoren.miaosha.dao.cache;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import top.zhaohaoren.miaosha.entity.Seckill;

/**
 * Dao下面不经可以放mysql的dao类
 * 也是可以放入redis的操作的
 */
public class RedisDao {
    private JedisPool jedisPool;


    //做一个类的模式，将redis序列化中的数据对象 通过反射对应到该类中 从而反序列化成其他对象
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }


    //获取商品的信息
    public Seckill getSeckill(long seckillId){
        //从缓存中获取数据
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckillId;
                byte[] bytes = jedis.get(key.getBytes()); // 使用jedis需要传入字节数组，获取的也是字节数组，都是序列化通信的
                if (bytes !=null){ // 缓存中有，从缓存中获取对象
                    Seckill seckill = schema.newMessage(); // 会创建一个空对象，对象里面是没有值的
                    ProtobufIOUtil.mergeFrom(bytes,seckill,schema); //ProtobufIOUtil工具类，反序列化==将数据byte值赋给seckill对象中
                    return seckill;
                }
            }finally {
                jedis.close(); //放回到连接池中去
            }
        }catch (Exception e){

        }
        return null;
    }

    //存入商品的信息，缓存中没有获取到对象，先从数据库中取出，然后存入到redis中
    public String putSeckill(Seckill seckill){
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getGoodId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 设置超时时间: 超过这个时间我们就将数据从redis中去除。使用了超时来维持数据一致性
                int timeout = 60*60;
                String result = jedis.setex(key.getBytes(),timeout,bytes); //失败返回错误信息，成功返回ok
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){

        }
        return null;
    }

}
