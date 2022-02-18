package com.pract.service;

import com.pract.domain.Device;

public interface DeviceService {
    public Device startReceiveData(Device device);

    public String close(Device device);

    public String getCurrentMsg(Device device);
}
