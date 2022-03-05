package com.pract.utils.mqtt;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;

@Data
public class ClientMqtt {
    private String HOST = null;   //必须
    private String TOPIC1 = null; //必须
    private int QOS = 0;          //必须
    private String CLIENT_ID = null; //必须
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = null;    //非必须
    private String passWord = null;  //非必须
    @SuppressWarnings("unused")
    private ScheduledExecutorService scheduler;
    private PushCallback pushCallback = new PushCallback();

    public ClientMqtt(String HOST, String TOPIC1, int QOS, String CLIENT_ID) {
        this.HOST = HOST;
        this.TOPIC1 = TOPIC1;
        this.QOS = QOS;
        this.CLIENT_ID = CLIENT_ID;
    }

    //使用new出来构造方法的默认参数设置进行连接
    public void start() {
        start(HOST, TOPIC1, QOS, CLIENT_ID);
    }

    //传递新参数，使用新参数赋值进行连接
    public void start(String host, String topics, int qos, String clientId) {
        HOST = host;
        TOPIC1 = topics;
        QOS = qos;
        CLIENT_ID = clientId;
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, CLIENT_ID, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置连接的用户名
            //options.setUserName(userName);
            // 设置连接的密码
            //options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 设置回调

            client.setCallback(pushCallback);
            MqttTopic topic = client.getTopic(TOPIC1);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//遗嘱        options.setWill(topic, "close".getBytes(), 2, true);
            client.connect(options);
            //订阅消息
            int[] Qos = {QOS};
            String[] topic1 = {TOPIC1};
            client.subscribe(topic1, Qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.client.disconnect();
            this.client.close(true);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getStandardMessage() {
        return pushCallback.getStandardMessage();
    }

    /*public static void main(String[] args) throws MqttException {
        ClientMqtt client = new ClientMqtt(null,null,0,null);
        client.start("tcp://test.ranye-iot.net:1883",
                    "Datlric-Pub-A:1",
                        0,
                    "zyp");

        System.out.println("客户端返回信息："+client.getStandardMessage());;
    }*/

}
