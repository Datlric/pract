package com.pract;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.pagehelper.PageHelper;
import com.pract.domain.Book;
import com.pract.domain.Device;
import com.pract.mapper.BookMapper;
import com.pract.mapper.DeviceMapper;
import com.pract.utils.JsonUtils;
import com.pract.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class PractApplicationTests {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private Environment env;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisUtils redisUtils;

    @Test
    void contextLoads() {

    }

    @Test
    void MybatisTest() {
        PageHelper.startPage(2, 6);
        List<Book> all = bookMapper.getAll();
        System.out.println(all);
    }

    @Test
    void JedisTest() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(2);
        jedis.set("name", "张三");
        jedis.expire("name", 60 * 2);
        String name = jedis.get("name");
        jedis.close();
        System.out.println(name);

       /* System.out.println(env.getProperty("spring.redis.database"));
        System.out.println(env.getProperty("spring.redis.host"));
        System.out.println(env.getProperty("spring.redis.port"));
        System.out.println(env.getProperty("spring.redis.timeout"));
        System.out.println(env.getProperty("spring.redis.jedis.pool.max-active"));
        System.out.println(env.getProperty("spring.redis.jedis.pool.max-wait"));*/
    }

    @Test
    void RedisUtilsTest() {
      /*  redisUtils.setExpire("name",60*3,"张三");
        HashMap<String, String> map = new HashMap<>();
        map.put("name","李四");
        map.put("gender","男");
        Map<String, String> user = redisUtils.Hset("user", map);
        redisUtils.setExpire("user",60*3);*/
       /* String hget = redisUtils.Hget("user", "name");
        Map<String, String> user = redisUtils.HgetAll("user");
        System.out.println(user);
        System.out.println(hget);*/
        String testToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NDQ4NjM2MDIsInVzZXJuYW1lIjoi5byg5LiJIn0.yuYpgthd5X_X3SQfN4uLCdoLLiqPBlWBIkEEh17c644";
        DecodedJWT decodedJWT = JWT.decode(testToken);
        Map<String, Claim> claims = decodedJWT.getClaims();
        for (String key : claims.keySet()) {
            Claim claim = claims.get(key);
            String payload = claim.asString();
            System.out.println(key + ": " + payload);
        }
    }

    @Test
    void deviceMapperTest() {
        List<Device> deviceList = deviceMapper.getAll();
        for (Device device : deviceList) {
            System.out.println(device);
        }
    }

    @Test
    void JsonUtilsTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("TOPIC", "5642231");
        map.put("QOS", "0");
        map.put("MESSAGE", "pressure:50");
        String s = JsonUtils.objectToJson(map);
        System.out.println(s);
    }

}
