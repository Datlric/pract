package com.pract.domain;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class OptLog {
    private String user_token;
    private String ip;
    private String type;
    private String description;
    private String model;
    private Timestamp opt_time;
    private String result;
}
