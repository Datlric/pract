package com.pract.utils.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;


public class PushCallback implements MqttCallback {
    String TOPIC = null;
    int QOS = 0;
    String MESSAGE = null;

    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，可以做重连");
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete---------" + token.isComplete());
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // subscribe后得到的消息会执行到这里面
        System.out.println("接收消息主题 : " + topic);
        TOPIC = topic;
        System.out.println("接收消息Qos : " + message.getQos());
        QOS = message.getQos();
        System.out.println("接收消息内容 : " + new String(message.getPayload()));
        MESSAGE = new String(message.getPayload());
        System.out.println("客户端返回信息：" + getStandardMessage());
    }

    public HashMap<String, String> getStandardMessage() {
        HashMap<String, String> map = new HashMap<>();
        map.put("TOPIC", TOPIC);
        map.put("QOS", String.valueOf(QOS));
        map.put("MESSAGE", MESSAGE);
        return map;
    }

}
