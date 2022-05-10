package com.pract.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pract.domain.Device;
import com.pract.domain.DeviceDataLog;
import com.pract.lifecycle.RunWith;
import com.pract.mapper.DeviceDataLogMapper;
import com.pract.mapper.DeviceMapper;
import com.pract.service.DeviceService;
import com.pract.utils.JsonUtils;
import com.pract.utils.RedisUtils;
import com.pract.utils.mqtt.ClientMqtt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DeviceDataLogMapper deviceDataLogMapper;

    @Autowired
    private DeviceMapper deviceMapper;

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
            try {
                //todo 2022/3/4 这里一定要让线程等待2秒！！！！，不然初始化还没完成就返回没有初始化结果的信息了，需要一定时间让mqtt消息到达并接受设置MESSAGE内容，mqtt消息到达间隔是3秒现在，可以改快
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //如果client对应获取信息map中的Message不为空的话，就将这个设备的信息放进deviceMap里面，RunWith类中会自动遍历
            String message = clientMqtt.getStandardMessage().get("MESSAGE");
            if (message != null) {
                //存储进全局工作设备信息的map，key: device_id; value: 客户端
                //这里有可能会由于RunWith中的轮循存数据方法正在工作而使deviceMap上锁，引起当前线程的阻塞等待
                RunWith.deviceMap.put(deviceForConnect.getDevice_id(), clientMqtt);
                // 将pushCallback与device_id放进map以便pushcallback丢失连接时能自动deviceMap以及更新redis内容
                RunWith.pushCallBackMap.put(clientMqtt.getPushCallback(), deviceForConnect.getDevice_id());
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
            //从接受信息实时设备map里删除此设备信息
            RunWith.deviceMap.remove(device.getDevice_id());
            //删除redis内存储此设备存放数据hash中的key、fields
            redisUtils.Hdel("data", device.getDevice_id());
            res = true;
        } else RunWith.deviceMap.remove(device.getDevice_id());
        return res;
    }

    @Override
    public String getCurrentMsg(Device device) {
        //todo 2.18 前端传参获取实时压力数据待写
        //从redis内取数据返回
        String message = null;
        try {
            String data = redisUtils.Hget("data", device.getDevice_id());
            if (!data.isEmpty()) {
                message = data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveDeviceData(DeviceDataLog deviceDataLog) {
        if (deviceDataLog == null) {
            return null;
        } else {
            return deviceDataLogMapper.saveOne(deviceDataLog);
        }
    }

    @Override
    public PageInfo<?> selectByPage(Integer pageNum, Integer pageSize, String search) {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("device_id");
        return new PageInfo<Device>(deviceMapper.getAll(search));
    }

    public Device selectOne(String device_id) {
        Device device = null;
        try {
            String deviceData = redisUtils.Hget("devices", device_id);
            if (!deviceData.isEmpty()) {
                device = JsonUtils.jsonToPojo(deviceData, Device.class);
            } else {
                //redis内查询不到从数据库拿
                device = deviceMapper.getOne(device_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device;
    }
}
