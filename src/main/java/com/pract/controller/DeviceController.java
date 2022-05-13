package com.pract.controller;

import com.github.pagehelper.PageInfo;
import com.pract.advisor.OperationLogAnnotation;
import com.pract.domain.Device;
import com.pract.service.DeviceService;
import com.pract.utils.RedisUtils;
import com.pract.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RedisUtils redisUtils;

    @OperationLogAnnotation(optModule = "设备模块-设备列表", optType = "查询", optDesc = "查询所有设备")
    @GetMapping("/findPage")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "5") Integer pageSize,
                           @RequestParam(defaultValue = "") String search) {
        PageInfo<?> deviceList = deviceService.selectByPage(pageNum, pageSize, search);
        return Result.success("查询成功", deviceList);
    }

    @PostMapping("/init")
    public Result initDeviceConnection(@RequestBody Device device) {
        if (device == null || device.getDevice_id().contains(" ") || device.getDevice_id() == null) {
            return Result.error("-1", "设备初始化失败", null);
        }
        Device initDevice = deviceService.startReceiveData(device);
        if (initDevice != null) {
            return Result.success("设备初始化成功", initDevice);
        }
        return Result.error("-1", "设备初始化失败", null);
    }

    @PostMapping("/close")
    public Result closeDevice(@RequestBody Device device) {
        Boolean closeFlag = deviceService.close(device);
        if (closeFlag) {
            return Result.success("关闭成功！", null);
        } else {
            return Result.error("-1", "关闭失败，请检查后台系统", null);
        }
    }

    @GetMapping("/getCurrentMsg")
    public Result getCurrentMsg(@RequestParam(defaultValue = "") String device_id) {
        Device device = new Device();
        device.setDevice_id(device_id);
        String currentMsg = deviceService.getCurrentMsg(device);
        return Result.success("此为接受到的实时消息", currentMsg);
    }

    @GetMapping("/findOne")
    public Result findOneDevice(@RequestParam(defaultValue = "") String device_id) {
        Device device = deviceService.selectOne(device_id);
        if (device != null) {
            return Result.success("查询成功", device);
        } else {
            return Result.error("-1", "未查找到相应设备", null);
        }
    }

    @GetMapping("/findDeviceIsStarted")
    public Result findDeviceIsStarted(@RequestParam(defaultValue = "") String device_id) {
        String data = null;
        data = redisUtils.Hget("data", device_id);
        if (data != null) {
            return Result.success("设备已启动", data);
        } else {
            return Result.error("-1", "设备未启动", null);
        }
    }
}
