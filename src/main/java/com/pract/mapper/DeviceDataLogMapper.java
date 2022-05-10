package com.pract.mapper;

import com.pract.domain.DeviceDataLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceDataLogMapper {

    public List<DeviceDataLog> getAllDataLog();

    public List<DeviceDataLog> getSingleDeviceLog(@Param("device_id") String device_id);

    public Integer saveOne(@Param("device_data") DeviceDataLog deviceDataLog);
}
