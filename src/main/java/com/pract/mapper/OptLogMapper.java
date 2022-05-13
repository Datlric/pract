package com.pract.mapper;

import com.pract.domain.OptLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OptLogMapper {
    public Integer saveOne(@Param("optlog") OptLog optLog);
}
