package com.pract.domain;

import lombok.Data;

@Data
public class Device {
    private String device_id;
    private String topic;
    private String host;
    private int qos;
    private String client_id;

    @Override
    public String toString() {
        return "Device{" +
                "device_id='" + device_id + '\'' +
                ", topic='" + topic + '\'' +
                ", host='" + host + '\'' +
                ", qos=" + qos +
                ", client_id='" + client_id + '\'' +
                '}';
    }
}
