package com.pract.lifecycle;

import com.pract.utils.JsonUtils;
import com.pract.utils.RedisUtils;
import com.pract.utils.mqtt.ClientMqtt;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RunWith implements ApplicationRunner, DisposableBean {
    //用于存储已经开始接受消息的mqtt客户端
    public static HashMap<String, ClientMqtt> deviceMap = new HashMap<>();

    private static Thread workThread;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //新开线程轮循对deviceMap内开启消息接受的设备进行数据存储到redis中,
        workThread = new Thread(() -> {
            //todo 2.18 需要写更新接受消息设备存储数据到redis的代码
            //todo 2.20 写完，需要硬件端联调，还有两个接口没写
            do {
                //线程未被打断执行以下代码
                try {
                    updateReceivedData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!Thread.interrupted());

            System.out.println("容器正常销毁进行，轮循数据更新工作线程打断");
        });

        workThread.start();
    }

    @Override
    public void destroy() throws Exception {
        workThread.interrupt();
        System.out.println("容器销毁,数据更新工作线程结束");
    }

    private void updateReceivedData() throws InterruptedException {
        //遍历设备信息集合，将收到的实时信息序列化Json以后，放进redis内，以Hash结构存储（"data",device_id,message）
        for (String device_id : deviceMap.keySet()) {
            ClientMqtt clientMqtt = deviceMap.get(device_id);
            String message = JsonUtils.objectToJson(clientMqtt.getStandardMessage());
            redisUtils.Hset("data", device_id, message);
        }
        //执行完一轮数据更新后暂停2秒，再执行下一轮，以防系统资源被此线程抢占过多
        Thread.sleep(2000);
    }
}
