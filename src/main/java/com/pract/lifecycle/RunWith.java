package com.pract.lifecycle;

import com.pract.domain.DeviceDataLog;
import com.pract.service.DeviceService;
import com.pract.utils.JsonUtils;
import com.pract.utils.RedisUtils;
import com.pract.utils.mqtt.ClientMqtt;
import com.pract.utils.mqtt.PushCallback;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RunWith implements ApplicationRunner, DisposableBean {
    //用于存储已经开始接受消息的mqtt客户端
    public static final HashMap<String, ClientMqtt> deviceMap = new HashMap<>();
    public static final HashMap<PushCallback, String> pushCallBackMap = new HashMap<>();

    private static Thread workThread;
    private RedisUtils redisUtils;
    @Autowired
    private DeviceService deviceService;

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("RunWith.run()主线程ID:  " + Thread.currentThread().getId());
        //新开线程轮循对deviceMap内开启消息接受的设备进行数据存储到redis中,
        workThread = new Thread(() -> {
            //todo 2.18 需要写更新接受消息设备存储数据到redis的代码
            //todo 2.20 写完，需要硬件端联调，还有两个接口没写
            do {
                //线程未被打断执行以下代码
                try {
                    System.out.println("updateReceivedData方法即将执行" + " ------from RunWith.run()" + "   时间戳：" + System.currentTimeMillis());
                    updateReceivedData();
                    System.out.println("updateReceivedData方法完成执行" + " ------from RunWith.run()" + "   时间戳：" + System.currentTimeMillis() + "  当前线程：" + Thread.currentThread().getId());
                    System.out.println("\n");
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
        System.out.println("updateReceivedData方法正在执行" + " ------from RunWith.updateReceivedData()" + "   时间戳：" + System.currentTimeMillis());
        //遍历已开始接受设备信息集合，将收到的实时信息序列化Json以后，放进redis内，以Hash结构存储（"data",device_id,message）
        if (deviceMap.size() != 0) {
            //用synchronized来对deviceMap进行加锁以保证向redis中缓存实时数据的过程的原子性、有序性
            //缺点：当deviceMap中的设备过多时，可能会用较长时间遍历并向redis内缓存完数据，此时如果有用户要调用启动设备的接口，有可能会引发接口的线程堵塞
            synchronized (deviceMap) {
                for (String device_id : deviceMap.keySet()) {
                    ClientMqtt clientMqtt = deviceMap.get(device_id);

                    System.out.println("     -----------" + "当前mqtt客户端hash：" + clientMqtt.hashCode());
                    //mqtt端传回来的信息
                    String message = JsonUtils.objectToJson(clientMqtt.getStandardMessage());

                    System.out.println("     -----------" + "接受到的消息：" + message);
                    //数据存入实时缓存中
                    /*Long saveResult =*/
                    redisUtils.Hset("data", device_id, message);
                    //数据存入MySQL中做备份
                    deviceService.saveDeviceData(new DeviceDataLog(device_id, message));

                    System.out.println("     -----------" + "共向redis中存入" + 1/*saveResult*/ + "条信息");
                }
            }
        }
        //执行完一轮数据更新后暂停3秒，再执行下一轮，以防系统资源被此线程抢占过多
        System.out.println("即将进入线程睡眠，当前线程ID：" + Thread.currentThread().getId());
        Thread.sleep(3000);
    }
}
