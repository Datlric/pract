package com.pract.mapper;

import com.pract.domain.Device;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceMapper {
    public List<Device> getAll();

    public List<Device> getAll(@Param("search") String search);

    public Device getOne(@Param("device_id") String device_id);
}
