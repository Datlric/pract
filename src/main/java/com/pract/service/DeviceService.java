package com.pract.service;

import com.pract.domain.Device;

public interface DeviceService {
    public String startReceiveData(Device device);

    public String close();
}
