package com.pract.controller;

import com.pract.domain.Device;
import com.pract.service.DeviceService;
import com.pract.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/findPage")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "5") Integer pageSize,
                           @RequestParam(defaultValue = "") String search) {

        return null;
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
            return Result.error("关闭失败，请检查后台系统", null);
        }
    }

    @GetMapping("/getCurrentMsg")
    public Result getCurrentMsg(@RequestBody Device device) {
        String currentMsg = deviceService.getCurrentMsg(device);
        return Result.success("此为接受到的实时消息", currentMsg);
    }
}
