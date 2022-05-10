package com.pract.utils.mqtt;

import com.pract.lifecycle.RunWith;
import com.pract.utils.RedisUtils;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Getter
@Component
public class PushCallback implements MqttCallback {
    String TOPIC = null;
    int QOS = 0;
    String MESSAGE = null;

    @Autowired
    private RedisUtils redisUtils;

    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，可以做重连");
        //MESSAGE设为null以示客户端已经丢失连接，可以进行重连
        //将deviceMap内的对应device清除，且将redis内的一并清除
        String device_id = RunWith.pushCallBackMap.get(this);
        ClientMqtt clientMqtt = RunWith.deviceMap.get(device_id);
        clientMqtt.close();
        //从接受信息实时设备map里删除此设备信息
        RunWith.deviceMap.remove(device_id);
        RunWith.pushCallBackMap.remove(device_id);
        //删除redis内存储此设备存放数据hash中的key、fields
        redisUtils.GlobalHdel("data", device_id);
        MESSAGE = null;
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete---------" + token.isComplete());
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // subscribe后得到的消息会执行到这里面
        //System.out.println("接收消息主题 : " + topic);
        TOPIC = topic;
        //System.out.println("接收消息Qos : " + message.getQos());
        QOS = message.getQos();
        //System.out.println("接收消息内容 : " + new String(message.getPayload()));
        MESSAGE = new String(message.getPayload());
        System.out.println("  ");
        System.out.println("客户端返回信息：" + getStandardMessage() + "-------from PushCallBack " + this.hashCode() + "  -----当前线程：" + Thread.currentThread().getId());
        System.out.println("  ");
    }

    public HashMap<String, String> getStandardMessage() {
        HashMap<String, String> map = new HashMap<>();
        map.put("TOPIC", TOPIC);
        map.put("QOS", String.valueOf(QOS));
        map.put("MESSAGE", MESSAGE);
        return map;
    }

}
