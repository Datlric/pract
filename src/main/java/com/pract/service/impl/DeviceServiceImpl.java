package com.pract.service.impl;

import com.pract.domain.Device;
import com.pract.lifecycle.RunWith;
import com.pract.service.DeviceService;
import com.pract.utils.JsonUtils;
import com.pract.utils.RedisUtils;
import com.pract.utils.mqtt.ClientMqtt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private RedisUtils redisUtils;

    //初次访问对设备进行连接以及确认信息收到
    @Override
    public Device startReceiveData(Device device) {
        //从redis内缓存的设备信息列表取单个设备数据
        String tmp = redisUtils.Hget("devices", device.getDevice_id());

        //将以json形式存储的设备信息对象转化为设备对象
        Device deviceForConnect = JsonUtils.jsonToPojo(tmp, Device.class);


        if (deviceForConnect != null) {
            ClientMqtt clientMqtt = new ClientMqtt(deviceForConnect.getHost(),
                    deviceForConnect.getTopic(),
                    deviceForConnect.getQos(),
                    deviceForConnect.getClient_id());
            //mqtt客户端进行连接
            clientMqtt.start();

            //如果client对应PushCallBack中的Message不为空的话，就将这个设备的信息放进deviceMap里面，RunWith类中会自动遍历
            if (!clientMqtt.getPushCallback().getMESSAGE().isEmpty()) {
                //存储进全局工作设备信息的map，key: device_id; value: 客户端
                RunWith.deviceMap.put(deviceForConnect.getDevice_id(), clientMqtt);
                return deviceForConnect;
            } else {
                //初始化接收到消息为空就关闭这个客户端
                clientMqtt.close();
            }
        }

        return null;
    }


    @Override
    public Boolean close(Device device) {
        boolean res = false;
        // todo 2.18 前端传参主动关闭设备待写
        ClientMqtt clientMqtt = RunWith.deviceMap.get(device.getDevice_id());
        if (clientMqtt != null) {
            clientMqtt.close();
            RunWith.deviceMap.remove(device.getDevice_id());
            res = true;
        } else RunWith.deviceMap.remove(device.getDevice_id());
        return res;
    }

    @Override
    public String getCurrentMsg(Device device) {
        //todo 2.18 前端传参获取实时压力数据待写
        HashMap<String, String> message = RunWith.deviceMap.get(device.getDevice_id()).getStandardMessage();
        return JsonUtils.objectToJson(message);
    }
}
