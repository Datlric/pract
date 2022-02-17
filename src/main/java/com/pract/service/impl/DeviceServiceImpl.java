package com.pract.service.impl;

import com.pract.domain.Device;
import com.pract.service.DeviceService;
import com.pract.utils.JsonUtils;
import com.pract.utils.RedisUtils;
import com.pract.utils.mqtt.ClientMqtt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DeviceServiceImpl implements DeviceService {

    //用于存储已经开始接受消息的mqtt客户端
    private HashMap<String, ClientMqtt> deviceMap = new HashMap<>();

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String startReceiveData(Device device) {
        String tmp = redisUtils.Hget("devices", device.getDevice_id());
        Device deviceForConnect = JsonUtils.jsonToPojo(tmp, Device.class);
        return null;
    }

    @Override
    public String close() {
        return null;
    }
}
