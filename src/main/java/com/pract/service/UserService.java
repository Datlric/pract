package com.pract.service;

import com.github.pagehelper.PageInfo;
import com.pract.domain.User;

import java.util.List;


public interface UserService {
    public List<User> getAll();

    public int SaveOneUser(User user) throws Exception;

    public PageInfo<?> selectByPage(Integer pageNum, Integer pageSize, String search);

    public Integer deleteUserInfo(Integer id);

    public Integer updateUserInfo(User user);

    public User userLogin(User user);
}
