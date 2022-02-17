package com.pract.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class Dispose implements DisposableBean {

    @Autowired
    private JedisPool jedisPool;

    @Override
    public void destroy() throws Exception {
        Jedis jedis = jedisPool.getResource();
        String db = jedis.flushDB();
        System.out.println("redis中关闭连接数据销毁执行结果：" + db + " ---------from destroy");
    }
}
