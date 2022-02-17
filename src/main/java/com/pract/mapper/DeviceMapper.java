package com.pract.mapper;

import com.pract.domain.Device;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceMapper {
    public List<Device> getAll();
}
