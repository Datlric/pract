package com.pract.service;

import com.github.pagehelper.PageInfo;
import com.pract.domain.Device;
import com.pract.domain.DeviceDataLog;

public interface DeviceService {
    public Device startReceiveData(Device device);

    public Boolean close(Device device);

    public String getCurrentMsg(Device device);

    public Integer saveDeviceData(DeviceDataLog deviceDataLog);

    public PageInfo<?> selectByPage(Integer pageNum, Integer pageSize, String search);

    public Device selectOne(String device_id);
}
