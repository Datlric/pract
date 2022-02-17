package com.pract.mapper;

import com.pract.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    public List<User> getAll();

    public List<User> getAll(@Param("search") String search);

    public int saveOne(@Param("user") User user);

    public int deleteById(@Param("id") Integer id);

    public int updateById(@Param("user") User user);

    public User selectOne(@Param("user") User user);

    public String selectUserByUsername(@Param("username") String username);
}
