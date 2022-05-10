package com.pract.domain;

import lombok.Data;

@Data
public class DeviceDataLog {
    private String device_id;
    private String message;

    public DeviceDataLog() {
    }

    public DeviceDataLog(String device_id, String message) {
        this.device_id = device_id;
        this.message = message;
    }
}
