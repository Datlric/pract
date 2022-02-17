package com.pract;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com.pract.mapper"})
@SpringBootApplication
public class PractApplication {

    public static void main(String[] args) {
        SpringApplication.run(PractApplication.class, args);
    }


}
