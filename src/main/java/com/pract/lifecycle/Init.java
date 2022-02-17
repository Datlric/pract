package com.pract.lifecycle;

import com.pract.domain.Device;
import com.pract.mapper.DeviceMapper;
import com.pract.utils.JsonUtils;
import com.pract.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * 容器初始化完成加载设备信息列表，接口取用设备列表中的信息进行连接时先进redis内查找，更新先更新mysql，之后调用更新方法更新redis
 */

@Component
public class Init implements ApplicationRunner {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("容器初始化完成" + redisUtils.getJedisPool());
        int length = updateDeviceList();
        System.out.println("已向redis中添加 " + length + " 条设备信息");
    }

    //更新设备信息
    public int updateDeviceList() {
        if (redisUtils.HgetAll("devices").size() != 0) {
            Long items = redisUtils.delKey("devices");
            System.out.println("设备信息列表删除 " + items + " 张列表");
        }
        List<Device> deviceList = deviceMapper.getAll();
        HashMap<String, String> devicesMap = new HashMap<>();
        for (Device device : deviceList) {
            devicesMap.put(device.getDevice_id(), JsonUtils.objectToJson(device));
        }
        return redisUtils.Hset("devices", devicesMap).size();
    }
}
