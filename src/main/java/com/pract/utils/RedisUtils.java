package com.pract.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Map;

@Component
public class RedisUtils {
    //redis的工具类封装

    @Autowired
    private JedisPool jedisPool;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String ans = null;
        try {
            ans = jedis.set(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            System.out.println("jedis客户端：" + jedis + "归还");
            jedis.close();
        }
        return ans;
    }

    public String setExpire(String key, int seconds, String value) {
        Jedis jedis = jedisPool.getResource();
        String ans = null;
        try {
            ans = jedis.setex(key, seconds, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }
        return ans;
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String ans = null;
        try {
            ans = jedis.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }
        return ans;
    }

    public Long Hset(String key, String filed, String value) {
        Jedis jedis = jedisPool.getResource();
        Long ans = null;
        try {
            ans = jedis.hset(key, filed, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }
        return ans;
    }

    public Map<String, String> Hset(String key, Map<String, String> map) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> ans = null;
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        try {
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                String entryKey = entry.getKey();
                String entryValue = entry.getValue();
                jedis.hset(key, entryKey, entryValue);
            }
            ans = jedis.hgetAll(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }

        return ans;
    }

    public String Hget(String key, String filed) {
        Jedis jedis = jedisPool.getResource();
        String ans = null;
        try {
            ans = jedis.hget(key, filed);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }
        return ans;
    }

    public Map<String, String> HgetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> ans = null;
        try {
            ans = jedis.hgetAll(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }
        return ans;
    }

    public Long setExpire(String key, int seconds) {
        Jedis jedis = jedisPool.getResource();
        Long expire = 0L;
        try {
            expire = jedis.expire(key, seconds);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }
        return expire;
    }

    public Long delKey(String key) {
        Jedis jedis = jedisPool.getResource();
        Long del = jedis.del(key);
        jedis.close();
        return del;
    }

    public Long Hdel(String key, String fileds) {
        Jedis jedis = jedisPool.getResource();
        Long hdel = jedis.hdel(key, fileds);
        jedis.close();
        return hdel;
    }
}

